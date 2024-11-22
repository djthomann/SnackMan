<template>
  <div ref="rendererContainer" class="canvas-container"></div>
</template>

<script lang="ts">
import { defineComponent, onUnmounted, ref, onMounted } from 'vue'
import eventBus from '@/services/eventBus'
import useWebSocket from '@/services/socketService'
import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import modelService from '@/services/modelService'

let scene: THREE.Scene
let wallsGroup: THREE.Group
// let floorGroup: THREE.Group
let foodGroup: THREE.Group

let bananaModel: THREE.Group
let appleModel: THREE.Group
let orangeModel: THREE.Group

let box: THREE.Mesh
let plane: THREE.Mesh
let sphere: THREE.Mesh

export default defineComponent({
  name: 'Scene',
  setup() {
    const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket()

    const rendererContainer = ref<HTMLDivElement | null>(null)
    const serverMessage = ref<string>('')
    let renderer: THREE.WebGLRenderer
    let camera: THREE.PerspectiveCamera
    let ambientLight: THREE.AmbientLight
    let directionalLight: THREE.DirectionalLight
    let controls: OrbitControls | null = null

    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message
      console.log('Processing server message')

      if (message.startsWith('MOVE')) {
        let key: string = message.split(':')[1]

        if (key === 'KeyD') {
          box.position.x += 0.2
        } else if (key === 'KeyA') {
          box.position.x -= 0.2
        } else if (key === 'KeyW') {
          box.position.z -= 0.2
        } else if (key === 'KeyS') {
          box.position.z += 0.2
        }
      } else if (message.startsWith('MAP')) {
        const map = JSON.parse(message.split(';')[1])
        loadMap(map)
      }
    }

    // Handle key press und send Event via WebSocket
    const handleKeyPress = (event: KeyboardEvent) => {
      sendMessage(`KEY:${event.code}`)
      console.log(`Key pressed: ${event.key}`)
    }

    document.addEventListener('keypress', handleKeyPress)

    onMounted(async () => {
      initScene()
      loadModels()
      eventBus.on('serverMessage', handleServerMessage)

      connect()

      window.addEventListener('resize', onWindowResize)
    })

    onUnmounted(() => {
      eventBus.off('serverMessage', handleServerMessage)

      closeConnection()

      // Three.js Cleanup
      controls?.dispose()
      renderer.dispose()
      if (rendererContainer.value && renderer.domElement) {
        rendererContainer.value.removeChild(renderer.domElement)
      }

      window.removeEventListener('resize', onWindowResize)
    })

    async function loadModels() {
      try {
        // Service initialisieren
        await modelService.initialize()

        // Modelle abrufen
        bananaModel = modelService.getModel('banana')
        bananaModel.scale.set(0.05, 0.05, 0.05)

        appleModel = modelService.getModel('apple')
        appleModel.scale.set(0.2, 0.2, 0.2)

        orangeModel = modelService.getModel('orange')
        orangeModel.scale.set(0.0025, 0.0025, 0.0025)

        console.log('Models loaded')
      } catch (error) {
        console.error('Error initializing or loading model:', error)
      }
    }

    function loadMap(map: any) {
      console.log('Received mapdata' + map)
      const w = map.w
      const h = map.h
      const tiles = map.allTiles

      for (const row of tiles) {
        for (const tile of row) {
          const occupationType = tile.occupationType
          if (occupationType == 'WALL') {
            // console.log(tile)
            wallsGroup.add(createWall(tile.x, tile.y))
          } else if (occupationType == 'ITEM') {
            foodGroup.add(createFood(tile.x, tile.y, Math.random() * 400 + 100))
          }
        }
      }

      scene.add(wallsGroup)
      wallsGroup.position.set(-(w / 2) + 0.5, 0, -(h / 2) + 0.5)

      scene.add(foodGroup)
      foodGroup.position.set(-(w / 2) + 0.5, 0, -(h / 2) + 0.5)

      const floor = createFloorTile(w, h)
      console.log('Creating Floor with: ' + w + '|' + h)
      scene.add(floor)
    }

    function initScene() {
      // Scene
      scene = new THREE.Scene()
      scene.background = new THREE.Color(0x111111)

      wallsGroup = new THREE.Group()
      // floorGroup = new THREE.Group()
      foodGroup = new THREE.Group()

      // Camera
      camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000)
      camera.position.set(5, 7.5, 7.5)

      // Renderer
      renderer = new THREE.WebGLRenderer({ antialias: true })
      renderer.setSize(window.innerWidth, window.innerHeight)
      renderer.shadowMap.enabled = true

      if (rendererContainer.value) {
        rendererContainer.value.appendChild(renderer.domElement)
      }

      // Dummy Box
      /*
      const boxGeometry = new THREE.BoxGeometry(1.5, 1.5, 1.5)
      const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f })
      box = new THREE.Mesh(boxGeometry, boxMaterial)
      box.position.set(0, 0, 0)
      box.castShadow = true
      scene.add(box)

      // Ground Plane
      const planeGeometry = new THREE.PlaneGeometry(20, 20, 20, 20)
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 })
      plane = new THREE.Mesh(planeGeometry, planeMaterial)
      plane.rotation.x = -Math.PI / 2
      plane.position.set(0, -3, 0)
      plane.receiveShadow = true
      scene.add(plane)
      */

      // Ambient Light
      ambientLight = new THREE.AmbientLight(0xffffff, 1)
      scene.add(ambientLight)

      // Directional Light
      directionalLight = new THREE.DirectionalLight(0xffffff, 1)
      directionalLight.position.set(0, 2, 0)
      directionalLight.castShadow = true
      scene.add(directionalLight)

      // Orbit Controls
      controls = new OrbitControls(camera, renderer.domElement)
      // controls.enableZoom = false

      // start Render-Loop
      animate()
    }

    function createFloorTile(x: number, y: number) {
      const planeGeometry = new THREE.PlaneGeometry(x, y, 1, 1)
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 })
      plane = new THREE.Mesh(planeGeometry, planeMaterial)
      plane.rotation.x = -Math.PI / 2
      plane.position.set(0, -0.5, 0)
      plane.receiveShadow = true

      return plane
    }

    function createWall(x: number, y: number) {
      const boxGeometry = new THREE.BoxGeometry(1, 1, 1)
      const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f })
      box = new THREE.Mesh(boxGeometry, boxMaterial)
      box.position.set(x, 0, y)
      box.castShadow = true

      return box
    }

    function createFood(x: number, y: number, calories: number) {
      if (calories > 300) {
        const newBanana = bananaModel.clone()
        newBanana.position.set(x, 0, y)
        return newBanana
      } else if (calories > 200) {
        const newApple = appleModel.clone()
        newApple.position.set(x, 0, y)
        return newApple
      } else {
        const newPear = orangeModel.clone()
        newPear.position.set(x, 0, y)
        return newPear
      }
    }

    function animate() {
      requestAnimationFrame(animate)

      const time = Date.now() * 0.001

      foodGroup.children.forEach((element, index) => {
        element.rotation.y += 0.01
        element.position.y = Math.sin(time * 2 + index) * 0.1
      })

      // bananaModel.rotation.y += 0.01

      controls?.update()
      renderer.render(scene, camera)
    }

    function onWindowResize() {
      camera.aspect = window.innerWidth / window.innerHeight
      camera.updateProjectionMatrix()
      renderer.setSize(window.innerWidth, window.innerHeight)
    }

    return {
      rendererContainer,
      serverMessage,
    }
  },
})
</script>

<style scoped>
html,
body {
  margin: 0;
  padding: 0;
}
.canvas-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
