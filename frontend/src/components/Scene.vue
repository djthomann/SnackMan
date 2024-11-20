<template>
  <div ref="rendererContainer" class="canvas-container"></div>
</template>

<script lang="ts">
import { defineComponent, onUnmounted, ref, onMounted } from 'vue'
import eventBus from '@/services/eventBus'
import useWebSocket from '@/services/socketService'
import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'

let scene: THREE.Scene
let wallsGroup: THREE.Group
let floorGroup: THREE.Group

export default defineComponent({
  name: 'Scene',
  setup() {
    const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket()

    const rendererContainer = ref<HTMLDivElement | null>(null)
    const serverMessage = ref<string>('')
    let renderer: THREE.WebGLRenderer
    let camera: THREE.PerspectiveCamera
    let box: THREE.Mesh
    let plane: THREE.Mesh
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
        const mapdata = JSON.parse(message.split(';')[1])
        console.log('Received mapdata' + mapdata)
        const tiles = mapdata.tiles
        const walls = mapdata.walls

        for (const tile of tiles) {
          floorGroup.add(createFloorTile(tile.x, tile.y))
          console.log('adding tile')
          scene.add(floorGroup)
        }

        for (const wall of walls) {
          wallsGroup.add(createWall(wall.x, wall.y))
          console.log('adding wall')
          scene.add(wallsGroup)
        }

        wallsGroup.position.set(-2, 0, -2)
        floorGroup.position.set(-2, 0, -2)
      }
    }

    // Handle key press und send Event via WebSocket
    const handleKeyPress = (event: KeyboardEvent) => {
      sendMessage(`KEY:${event.code}`)
      console.log(`Key pressed: ${event.key}`)
    }

    document.addEventListener('keypress', handleKeyPress)

    onMounted(() => {
      initScene()
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

    function initScene() {
      // Scene
      scene = new THREE.Scene()
      scene.background = new THREE.Color(0x111111)

      wallsGroup = new THREE.Group()
      floorGroup = new THREE.Group()

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

      // start Render-Loop
      animate()
    }

    function createFloorTile(x: number, y: number) {
      const planeGeometry = new THREE.PlaneGeometry(1, 1, 1, 1)
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 })
      plane = new THREE.Mesh(planeGeometry, planeMaterial)
      plane.rotation.x = -Math.PI / 2
      plane.position.set(x, -0.5, y)
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

    function animate() {
      requestAnimationFrame(animate)
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
