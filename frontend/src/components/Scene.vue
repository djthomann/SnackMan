<template>
  <div ref="rendererContainer" class="canvas-container">
    <button id="startButton">Start</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, onUnmounted, ref, onMounted } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';
import * as THREE from 'three';
import { PointerLockControls } from 'three/examples/jsm/Addons.js';
import modelService from '@/services/modelService';
import type { Snackman, Ghost, Food, Tile } from '@/types/SceneTypes';
import { useEntityStore } from '@/stores/entityStore';
import { storeToRefs } from 'pinia';

// Groups of different map objects
let wallsGroup: THREE.Group;
// let floorGroup: THREE.Group
let foodGroup: THREE.Group;

// Models from modelService
let bananaModel: THREE.Group;
let appleModel: THREE.Group;
let orangeModel: THREE.Group;

// mesh for walls
let box: THREE.Mesh;

const mapScale = 1

/* for fallback purposes if no model is loaded
let plane: THREE.Mesh;
let sphere: THREE.Mesh;
*/

export default defineComponent({
  name: 'Scene',
  setup() {
    const { sendMessage } = useWebSocket();

    const rendererContainer = ref<HTMLDivElement | null>(null);
    const serverMessage = ref<string>('');
    const player = new THREE.Mesh();
    let renderer: THREE.WebGLRenderer;
    let camera: THREE.PerspectiveCamera;
    let scene: THREE.Scene;
    let cone: THREE.Mesh;
    let plane: THREE.Mesh;
    let ambientLight: THREE.AmbientLight;
    let directionalLight: THREE.DirectionalLight;
    let controls: PointerLockControls;
    let mouseMovement = false;


    //GameStart
    const entityStore = useEntityStore();
    const { snackmen, ghosts } = storeToRefs(entityStore);


    console.log('Snackman Names:', snackmen.value.map((item: Snackman) => item.username));


    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message;
      console.log('Processing server message');

      if (message.startsWith('MOVE')) {
        let key: string = message.split(':')[1];

        // Move the player
        movePlayer(JSON.parse(message.split(';')[1]))

      /*  if (key === 'KeyD') {
          cone.position.x += 0.2;
        } else if (key === 'KeyA') {
          cone.position.x -= 0.2;
        } else if (key === 'KeyW') {
          cone.position.z -= 0.2;
        } else if (key === 'KeyS') {
          cone.position.z += 0.2;
        }
          */
      } else if (message.startsWith('MAP')) {
        console.log('processing map');
        const map = JSON.parse(message.split(';')[1]);
        loadMap(map);
      }
    };

    onMounted(async () => {
      initScene();
      loadModels();
      eventBus.on('serverMessage', handleServerMessage);

      window.addEventListener('resize', onWindowResize);
    });

    onUnmounted(() => {
      eventBus.off('serverMessage', handleServerMessage);

      // Three.js Cleanup
      controls?.dispose();
      renderer.dispose();
      if (rendererContainer.value && renderer.domElement) {
        rendererContainer.value.removeChild(renderer.domElement);
      }

      window.removeEventListener('resize', onWindowResize);
    });

    async function loadModels() {
      try {
        // Service initialisieren
        await modelService.initialize();

        // Modelle abrufen
        bananaModel = modelService.getModel('banana');
        bananaModel.scale.set(0.05, 0.05, 0.05);

        appleModel = modelService.getModel('apple');
        appleModel.scale.set(0.2, 0.2, 0.2);

        orangeModel = modelService.getModel('orange');
        orangeModel.scale.set(0.0025, 0.0025, 0.0025);

        console.log('Models loaded');
      } catch (error) {
        console.error('Error initializing or loading model:', error);
      }
    }

    function loadMap(map: any) {
      console.log('Received mapdata' + map);
      const w = map.w * mapScale;
      const h = map.h * mapScale;
      const tiles = map.allTiles;

      for (const row of tiles) {
        for (const tile of row) {
          const occupationType = tile.occupationType;
          if (occupationType == 'WALL') {
            // console.log(tile)
            wallsGroup.add(createWall(tile.x, tile.y));
          } else if (occupationType == 'ITEM') {
            foodGroup.add(createFood(tile.x, tile.y, Math.random() * 400 + 100));
          }
        }
      }


      scene.add(wallsGroup);
      //wallsGroup.position.set(-(w / 2) + 0.5, 0, -(h / 2) + 0.5); // Center objects
      //wallsGroup.position.set(-20, 0, -20);

      scene.add(foodGroup);
      //foodGroup.position.set(-(w / 2) + 0.5, 0, -(h / 2) + 0.5); // Center objects
      //foodGroup.position.set(-20, 0, -20);

      const floor = createFloorTile(w, h);
      // console.log('Creating Floor with: ' + w + '|' + h);
      scene.add(floor);
    }

    /**
     * The camera is moved to the updated position when the w-key is pressed
     */
    function movePlayer(moveInformation: any) {

        const newPlayerPositionX = moveInformation.movementVector.x;
        const newPlayerPositionY = moveInformation.movementVector.y;
        const newPlayerPositionZ = moveInformation.movementVector.z;

        console.log(`New player position after move event was sent back from the server: x = ${newPlayerPositionX}, y = ${newPlayerPositionY}, z = ${newPlayerPositionZ}`)

        player.position.set(newPlayerPositionX, newPlayerPositionY, newPlayerPositionZ);
    }

    function initScene() {
      // Scene
      scene = new THREE.Scene();
      scene.background = new THREE.Color(0x111111);

      wallsGroup = new THREE.Group();
      // floorGroup = new THREE.Group()
      foodGroup = new THREE.Group();

      // Camera
      camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);

      // Vectors
      const forward = new THREE.Vector3();
      camera.getWorldDirection(forward);
      forward.normalize();

      // Renderer
      renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setSize(window.innerWidth, window.innerHeight);
      renderer.shadowMap.enabled = true;

      if (rendererContainer.value) {
        rendererContainer.value.appendChild(renderer.domElement);
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
      ambientLight = new THREE.AmbientLight(0xffffff, 1);
      scene.add(ambientLight);

      // Directional Light
      directionalLight = new THREE.DirectionalLight(0xffffff, 1);
      directionalLight.position.set(0, 2, 0);
      directionalLight.castShadow = true;
      scene.add(directionalLight);

      // Player Body
      const coneGeometry = new THREE.ConeGeometry(0.3, 0.5, 32);
      const coneMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
      cone = new THREE.Mesh(coneGeometry, coneMaterial);
      cone.position.set(0 - mapScale / 2, 0, 0 - mapScale / 2);
      camera.position.set(0 - mapScale / 2, 0.5, 0 - mapScale / 2);
      cone.rotation.x = -Math.PI / 2;
      cone.castShadow = true;
      scene.add(cone);

      // Player Object
      scene.add(player);
      player.add(camera);
      player.add(cone);

      // PointerLock Controls
      controls = new PointerLockControls(camera, renderer.domElement);
      const startButton = document.getElementById('startButton') as HTMLInputElement;
      player.add(controls.object);
      startButton.addEventListener(
        'click',
        function () {
          controls.lock();
        },
        false,
      );

      // Hide and unhide start button
      controls.addEventListener('lock', () => {
        startButton.classList.add('hidden'); // Button verstecken
      });

      controls.addEventListener('unlock', () => {
        startButton.classList.remove('hidden'); // Button anzeigen
      });

      // TODO When entering a user name no move event should be sent to the backend

      let keyPressedArray: string[] = []

      /* Adds keys to the keyPressedArray when one of the specific movement keys is pressed */
      document.addEventListener('keydown', (event) => {

        if (['w', 'a', 's', 'd', ' '].includes(event.key)) {
          if (keyPressedArray.indexOf(event.key) === -1) {
            keyPressedArray.push(event.key)
          }

        }

      })

      /* Removes movement key from the keyPressedArray when key is let go */
      document.addEventListener('keyup', (event) => {

        if (['w', 'a', 's', 'd', ' '].includes(event.key)) {

            const index = keyPressedArray.indexOf(event.key);
            if (index > -1) {
              keyPressedArray.splice(index, 1);
            }  
        }

      })

      /* Calculates movementVector depending on the pressed keys (= keys in the keyPressedArray) */
      function handleMovement() {

          let forward = new THREE.Vector3(0, 0, 0);
          forward = camera.getWorldDirection(forward);
          forward.y = 0;
          forward.normalize()
          let vector = new THREE.Vector3(0, 0, 0);
          const angle = Math.PI / 2;
          const rotationAxis = new THREE.Vector3(0, 1, 0);

          if(keyPressedArray.includes('w')) {
            vector = vector.add(forward.clone())
          }

          if(keyPressedArray.includes('a')) {
            vector = vector.add(forward.clone().applyAxisAngle(rotationAxis, angle).normalize())
          }

          if(keyPressedArray.includes('s')) {
            vector = vector.add(forward.clone().negate())
          }

          if(keyPressedArray.includes('d')) {
            vector = vector.add(forward.clone().applyAxisAngle(rotationAxis, -angle).normalize())
          }

          if(keyPressedArray.includes(' ')) {
            vector = vector.add(new THREE.Vector3(0, 1, 0))
          }

          vector?.normalize();
          console.log('Vector:', vector)
          
          const data = JSON.stringify({
          type: "MOVE",
          gameID: 0,
          objectID: 0,
          movementVector: vector
          });

          sendMessage(data);
        
      }

      // Calls the handleMovement function in a specified time interval
      setInterval(handleMovement, 50);


      document.addEventListener('mousemove', () => {
        mouseMovement = true;
      });

      // start Render-Loop
      animate();
    }

    // Creates one large plane as the floor
    function createFloorTile(x: number, y: number) {
      const planeGeometry = new THREE.PlaneGeometry(x, y, 1, 1);
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 });
      plane = new THREE.Mesh(planeGeometry, planeMaterial);
      plane.rotation.x = -Math.PI / 2;
      plane.position.set(x / 2, -0.5, y / 2);
      plane.receiveShadow = true;

      return plane;
    }

    // Creates one cube per wall tile
    function createWall(x: number, y: number) {
      const boxGeometry = new THREE.BoxGeometry(1 * mapScale, 3, 1 * mapScale);
      const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
      box = new THREE.Mesh(boxGeometry, boxMaterial);
      box.position.set(x * mapScale, 0, y * mapScale);
      box.castShadow = true;

      return box;
    }

    // Creates Food item, chooses model depending on calories --> randomnly generated in frontend right now (not good)
    function createFood(x: number, y: number, calories: number) {
      let newModel;
      if (calories > 300) {
        newModel= bananaModel.clone();
      } else if (calories > 200) {
        newModel = appleModel.clone();
      } else {
        newModel = orangeModel.clone();
      }
      newModel.position.set(x * mapScale, 0, y * mapScale)
      return newModel
    }

    function animate() {
      requestAnimationFrame(animate);
      rotateBody();

      const time = Date.now() * 0.001;

      // Animates food objects, has to loop over entire group at the moments --> better option avaible if performance sucks
      foodGroup.children.forEach((element, index) => {
        element.rotation.y += 0.01;
        element.position.y = Math.sin(time * 2 + index) * 0.1;
      });

      renderer.render(scene, camera);
    }

    function onWindowResize() {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    }

    // Method to turn the player body according to camera forward direction
    function rotateBody() {
      const forward = new THREE.Vector3();
      const playerForward = new THREE.Vector3();
      camera.getWorldDirection(forward);
      player.getWorldDirection(playerForward);
      forward.normalize();
      playerForward.normalize();

      if (controls.isLocked) {
        if (mouseMovement) {
          const angleYCameraDirection = Math.atan2(
            playerForward.x - forward.x,
            playerForward.z - forward.z,
          );

          // Interpolation for smooth rotation
          const smoothingFactor = 0.1;
          const currentAngle = cone.rotation.z;

          // Player body facing forward
          if (forward.z < 0 && angleYCameraDirection < 0.125 && angleYCameraDirection > -0.125) {
            //console.log('Face Forward');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, 0, smoothingFactor);

            // Player body facing forward-right
          } else if (
            forward.z < 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
            //console.log('Turn Forward Right');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 4, smoothingFactor);

            // Player body facing right
          } else if (angleYCameraDirection < -0.375) {
            //console.log('Turn Right');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 2, smoothingFactor);

            // Player body facing backwards-right
          } else if (
            forward.z > 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
            //console.log('Turn Backward Right');
            cone.rotation.z = THREE.MathUtils.lerp(
              currentAngle,
              -Math.PI / 2 - Math.PI / 4,
              smoothingFactor,
            );

            // Player body facing backwards
          } else if (
            forward.z > 0 &&
            angleYCameraDirection < 0.125 &&
            angleYCameraDirection > -0.125
          ) {
            //console.log('Turn Backwards');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI, smoothingFactor);

            // Player body facing backwards-left
          } else if (
            forward.z > 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
            //console.log('Turn Backward Left');
            cone.rotation.z = THREE.MathUtils.lerp(
              currentAngle,
              Math.PI / 2 + Math.PI / 4,
              smoothingFactor,
            );

            // Player body facing left
          } else if (angleYCameraDirection > 0.375) {
            //console.log('Turn Left');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 2, smoothingFactor);

            // Player body facing forward-left
          } else if (
            forward.z < 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
            //console.log('Turn Forward Left');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 4, smoothingFactor);
          }
        }
      }
    }

    return {
      rendererContainer,
      serverMessage,
    };
  },
});
</script>

<style scoped>
html,
body {
  margin: 0;
  padding: 0;
}

.canvas-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

#startButton {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  z-index: 10;
  display: block;
}

#startButton.hidden {
  display: none;
}
</style>
