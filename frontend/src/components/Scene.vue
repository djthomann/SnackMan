<template>
  <GameOverlay ref="gameOverlayRef" />
  <div ref="rendererContainer" class="canvas-container">
    <button id="startButton">play</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, onUnmounted, ref, onMounted, nextTick } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';
import * as THREE from 'three';
import { PointerLockControls } from 'three/examples/jsm/Addons.js';
import modelService from '@/services/modelService';
import type { Ghost, Snackman } from '@/types/SceneTypes';
import { useEntityStore } from '@/stores/entityStore';
import { useGameStore } from '@/stores/gameStore';
import { storeToRefs } from 'pinia';
import NameTag from '@/services/nameTagService';
import skybox_ftURL from '@/assets/images/skybox/skybox_ft.png';
import skybox_bkURL from '@/assets/images/skybox/skybox_bk.png';
import skybox_upURL from '@/assets/images/skybox/skybox_up.png';
import skybox_dnURL from '@/assets/images/skybox/skybox_dn.png';
import skybox_lfURL from '@/assets/images/skybox/skybox_lf.png';
import skybox_rtURL from '@/assets/images/skybox/skybox_rt.png';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { Mesh } from 'three';
import GameOverlay from './GameOverlay.vue';

import { Logger } from '../util/logger';

// Groups of different map objects
let wallsGroup: THREE.Group;
let floorGroup: THREE.Group;
let foodGroup: THREE.Group;
let chickenGroup: THREE.Group;

// Textures for Skybox
const skyboxTextures: THREE.MeshBasicMaterial[] = [];
const texture_ft = new THREE.TextureLoader().load(skybox_ftURL);
texture_ft.colorSpace = THREE.SRGBColorSpace;
const texture_bk = new THREE.TextureLoader().load(skybox_bkURL);
texture_bk.colorSpace = THREE.SRGBColorSpace;
const texture_up = new THREE.TextureLoader().load(skybox_upURL);
texture_up.colorSpace = THREE.SRGBColorSpace;
const texture_dn = new THREE.TextureLoader().load(skybox_dnURL);
texture_dn.colorSpace = THREE.SRGBColorSpace;
const texture_rt = new THREE.TextureLoader().load(skybox_rtURL);
texture_rt.colorSpace = THREE.SRGBColorSpace;
const texture_lf = new THREE.TextureLoader().load(skybox_lfURL);
texture_lf.colorSpace = THREE.SRGBColorSpace;

skyboxTextures.push(new THREE.MeshBasicMaterial({ map: texture_ft }));
skyboxTextures.push(new THREE.MeshBasicMaterial({ map: texture_bk }));
skyboxTextures.push(new THREE.MeshBasicMaterial({ map: texture_up }));
skyboxTextures.push(
  new THREE.MeshBasicMaterial({
    map: texture_dn,
    transparent: true,
    opacity: 0,
  }),
);
skyboxTextures.push(new THREE.MeshBasicMaterial({ map: texture_rt }));
skyboxTextures.push(new THREE.MeshBasicMaterial({ map: texture_lf }));

console.log('textures found', texture_ft.image); // Should not be null or undefined

// mesh for walls
let box: THREE.Mesh;

const mapScale = 5;
const wallHeight = 1 * mapScale;

export default defineComponent({
  components: {
    GameOverlay,
  },
  name: 'Scene',
  setup() {
    const gameOverlayRef = ref<InstanceType<typeof GameOverlay> | null>(null);

    const { sendMessage } = useWebSocket();

    const rendererContainer = ref<HTMLDivElement | null>(null);
    const serverMessage = ref<string>('');
    const player = new THREE.Mesh();
    const route = useRoute();
    const userStore = useUserStore();
    let renderer: THREE.WebGLRenderer;
    let camera: THREE.PerspectiveCamera;
    let scene: THREE.Scene;
    let cone: THREE.Mesh;
    let plane: THREE.Mesh;
    let ambientLight: THREE.AmbientLight;
    let directionalLight: THREE.DirectionalLight;
    let controls: PointerLockControls;
    let mouseMovement = false;
    let nameTag: NameTag;
    const nameTags: NameTag[] = [];
    const animationMixers: THREE.AnimationMixer[] = [];
    
    let gameID = 2;
    let startPromiseResolve: () => void;
    let testingMode = false;

    const logger = new Logger();

    //GameStart
    const entityStore = useEntityStore();
    const gameStore = useGameStore();
    const { snackMen, ghosts, chicken, map } = storeToRefs(entityStore);
    const meshes: Map<Number, Mesh> = new Map<Number, Mesh>();

    logger.info(
      'Snackman Names:',
      [...snackMen.value.values()].map((item: Snackman) => item.username),
    );

    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message;

      if (message.startsWith('MOVE')) {
        testingMode = true;
        const key: string = message.split(':')[1];

        // Move the player
        movePlayer(JSON.parse(message.split(';')[1]));
      } else if (message.startsWith('MAP')) {
        logger.info('processing map');
        testingMode = true;
        const map = JSON.parse(message.split(';')[1]);
        loadMap(map);
      } else if (message.startsWith('DISAPPEAR')) {
        const food = JSON.parse(message.split(';')[1]);
        makeDisappear(food.food.objectId);
        updateCalories(100);
      } else if (message.startsWith('GAME_START')) {
        handleStartEvent();
        if (startPromiseResolve) {
          startPromiseResolve();
        }
        //const line = JSON.parse(message.split(';')[1]);     // Need a new Solution !
        //for (const chicken of line.chicken) {
        //  loadChicken(chicken);
        //}
      } else if (message.startsWith('GAME_STATE')) {
        handleGameStateEvent(message.split(';')[1]);
      }
    };

    const updateCalories = (amount: number) => {
      gameStore.addCalories(amount);
    };

    const updateTime = (sec: number) => {
      gameStore.setRemainingTime(sec);
    };

    const handleGameStateEvent = (message: string) => {
      const parsedData = JSON.parse(message);
      parsedData.updatesSnackMen.forEach((snackman: Snackman) => {
        meshes
          .get(snackman.objectId)!
          .position.set(snackman.x * mapScale, snackman.y * mapScale, snackman.z * mapScale);
      });

      parsedData.updatesGhosts.forEach((ghost: Ghost) => {
        meshes.get(ghost.objectId)!.position.set(ghost.x * mapScale, ghost.y * mapScale, ghost.z * mapScale);
      })

      for (const chicken of parsedData.updatesChicken){
          console.log("chicken-id", chicken.objectId); 
          move(chicken.objectId, chicken.x, chicken.y, chicken.z);
        }

    }

    const handleStartEvent = () => {
      console.log('handle start event');
      loadMap(map.value);
    };

    const waitForStartMessage = () => {
      return new Promise<void>((resolve) => {
        startPromiseResolve = resolve;
      });
    };

    const requestMap = () => {
      console.log('loading map');
      const requestData = {
        type: 'MAPREQUEST',
        id: gameID,
      };
      sendMessage(JSON.stringify(requestData));
    };

    function move(id: number, x: number, y: number, z: number) {
      chickenGroup.children.forEach((chicken) => {
        if(chicken.userData.id === id) {
          // Calculate the movement vector
          const moveX = x - chicken.position.x / mapScale;
          const moveZ = z - chicken.position.z / mapScale;
          // Update position
          chicken.position.set(
            x * mapScale, 
            y * mapScale, 
            z * mapScale
          ); 
          // Calculate and apply rotation
          if (moveX !== 0 || moveZ !== 0) {
            const rotationY = Math.atan2(moveX, moveZ);
            chicken.rotation.y = rotationY;
          }
        }
      })
    }

    onMounted(async () => {
      await nextTick();
      console.log(gameOverlayRef.value);
      eventBus.on('serverMessage', handleServerMessage);

      try {
        // Service initialisieren
        await modelService.initialize(mapScale);
      } catch (error) {
        console.error('Error initializing or loading model:', error);
      }
      loadModels();
      initScene();

      await waitForStartMessage();

      loadPlayerEntities([...snackMen.value.values()], [...ghosts.value.values()], scene);

      window.addEventListener('resize', onWindowResize);

      const lastSegment = route.path.split('/').pop() || '';
      if (/^\d+$/.test(lastSegment)) {
        gameID = Number(lastSegment);
      }
      console.log('scene with gameID ' + gameID);

      // requestMap();
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
        await modelService.initialize(mapScale);

        logger.info('Models loaded');
      } catch (error) {
        console.error('Error initializing or loading model:', error);
      }
    }

    function loadChicken(newChicken: any) {
      const chicken = modelService.createChicken(newChicken.objectId, newChicken.x * mapScale, newChicken.z * mapScale); 
      chickenGroup.add(chicken); 

      const chickenMixer = new THREE.AnimationMixer(chicken);
      const chickenAnimations = modelService.getAnimations('chicken');

      if (chickenAnimations.length > 0) {
        const action = chickenMixer.clipAction(chickenAnimations[0]);
        action.play();
      } else {
        console.log('Animation not found');
      }

      // Mixer is stored in a global list, so that it can be used in the update-loop 
      animationMixers.push(chickenMixer);
    }

    /*
      Ghosts and Snackmen are spawned on the correct position
    */

    function loadPlayerEntities(snackMen: Snackman[], ghosts: Ghost[], scene: any) {
      // Group for snackMen and ghosts
      const snackMenGroup = new THREE.Group();
      const ghostsGroup = new THREE.Group();

      // Iterate over snackMen and add them to the scene
      snackMen.forEach((snackMan) => {
        const snackManGeometry = new THREE.SphereGeometry(1, 32, 32);
        const snackManMaterial = new THREE.MeshStandardMaterial({ color: 0x00ff00 });
        const snackManMesh = new THREE.Mesh(snackManGeometry, snackManMaterial);

        // Position snackMan
        snackManMesh.position.set(snackMan.x, mapScale / 2, snackMan.z);

        // Attach a NameTag
        const snackManTag = new NameTag(snackMan.username, snackManMesh, scene);
        nameTags.push(snackManTag);

        // Add to snackMen group
        snackMenGroup.add(snackManMesh);

        if (!testingMode && snackMan.objectId == userStore.id) {
          snackManMesh.add(camera);
          camera.position.set(0, 0, 0);
        }

        meshes.set(snackMan.objectId, snackManMesh);
        console.log(`placed Snackman ${snackMan.objectId} on Scene`);
      });

      // Iterate over ghosts and add them to the scene
      ghosts.forEach((ghost) => {
        const ghostGeometry = new THREE.ConeGeometry(1, 2, 32);
        const ghostMaterial = new THREE.MeshStandardMaterial({ color: 0xff0000 });
        const ghostMesh = new THREE.Mesh(ghostGeometry, ghostMaterial);

        // Position ghost
        ghostMesh.position.set(ghost.x, mapScale / 2, ghost.z);

        const ghostTag = new NameTag(ghost.username || 'Ghost', ghostMesh, scene);
        nameTags.push(ghostTag);

        // Add to ghosts group
        ghostsGroup.add(ghostMesh);
        meshes.set(ghost.objectId, ghostMesh);
      });

      // Add groups to the scene
      scene.add(snackMenGroup);
      scene.add(ghostsGroup);
    }

    function loadMap(m: any) {
      console.log('Received mapdata' + m);
      const w = m.w * mapScale;
      const h = m.h * mapScale;
      const tiles = m.tileRecords;

      for (const row of tiles) {
        for (const tile of row) {
          const occupationType = tile.occupationType;
          if (occupationType == 'WALL') {
            wallsGroup.add(modelService.createWall(tile.x, tile.z, mapScale, wallHeight));
          } else if (occupationType == 'ITEM') {
            const food = modelService.createFood(tile.food.objectId, tile.x, tile.z, Math.random() * 400 + 100, mapScale);
            food.userData.id = tile.food.objectId;
            foodGroup.add(food);
            floorGroup.add(modelService.createFloorTile(tile.x, tile.z, mapScale));
          } else if (occupationType == 'FREE') {
            const occupation = tile.occupation;
            floorGroup.add(modelService.createFloorTile(tile.x, tile.z, mapScale));
          }
        }
      }
      scene.add(chickenGroup);      
      scene.add(wallsGroup);
      scene.add(foodGroup);
      scene.add(floorGroup);

      // Skybox
      for (let i = 0; i < 6; i++) {
        skyboxTextures[i].side = THREE.BackSide;
        skyboxTextures[i].transparent = true;
      }
      const skyboxGeo = new THREE.BoxGeometry(w, w/4, w)
      const skybox = new THREE.Mesh(skyboxGeo, skyboxTextures);
      console.log('skybox position', skybox.position)
      skybox.position.y = ((w/4)/2);
      skybox.position.x = w/2;
      skybox.position.z = w/2;
      scene.add(skybox);

      player.position.set(w / 2, mapScale, h / 2);
    }

    /**
     * The camera is moved to the updated position when the w-key is pressed
     */
    function movePlayer(moveInformation: any) {
      const newPlayerPositionX = moveInformation.movementVector.x;
      const newPlayerPositionY = moveInformation.movementVector.y;
      const newPlayerPositionZ = moveInformation.movementVector.z;

      logger.info(
        '`New player position after move event was sent back from the server: x = ${newPlayerPositionX}, y = ${newPlayerPositionY}, z = ${newPlayerPositionZ}`',
      );

      player.position.set(
        (newPlayerPositionX+0.5) * mapScale,
        newPlayerPositionY,
        (newPlayerPositionZ+0.5) * mapScale,
      );
    }

    function makeDisappear(id: number) {
      updateCalories(100);
      updateTime(id);
      foodGroup.children.forEach((food) => {
        if (food.userData.id == id) {
          scene.remove(food);
          foodGroup.remove(food);
          console.log(`food with Id ${id} disappeared juhu`);
        }
      });
    }

    function initScene() {
      // Scene
      scene = new THREE.Scene();
      scene.background = new THREE.Color(0x111111);

      wallsGroup = new THREE.Group();
      floorGroup = new THREE.Group();
      foodGroup = new THREE.Group();
      chickenGroup = new THREE.Group();

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

      // Test cylinder
      //const geometry = new THREE.CylinderGeometry(0.2 * mapScale, 0.2 * mapScale, 3, 32);
      //const material = new THREE.MeshStandardMaterial({ color: 0x00ff00 });
      //const cylinder = new THREE.Mesh(geometry, material);
      //cylinder.position.set( 20.5 * mapScale , 0, 20.5 * mapScale );
      //scene.add(cylinder);

      // Test Body for Username Test
      const testObj = new THREE.Mesh(coneGeometry, coneMaterial);
      testObj.position.set(0 - mapScale / 2, 0, 0 - mapScale / 2);
      testObj.rotation.x = -Math.PI / 2;
      testObj.castShadow = true;
      scene.add(testObj);

      // Test Player for Username Test
      const testPlayer = new THREE.Mesh();
      testPlayer.position.set(0 - mapScale / 2, 0, 0 - mapScale / 2);
      scene.add(testPlayer);
      testPlayer.add(testObj);

      // Create NameTag
      nameTag = new NameTag('Snacko', testPlayer, scene);
      nameTags.push(nameTag);
      nameTags.push(nameTag);

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

      const keyPressedArray: string[] = [];

      /* Adds keys to the keyPressedArray when one of the specific movement keys is pressed */
      document.addEventListener('keydown', (event) => {
        if (['w', 'a', 's', 'd', ' '].includes(event.key)) {
          if (keyPressedArray.indexOf(event.key) === -1) {
            keyPressedArray.push(event.key);
          }
        }
      });

      /* Removes movement key from the keyPressedArray when key is let go */
      document.addEventListener('keyup', (event) => {
        if (['w', 'a', 's', 'd', ' '].includes(event.key)) {
          const index = keyPressedArray.indexOf(event.key);
          if (index > -1) {
            keyPressedArray.splice(index, 1);
          }
        }
      });

      /* Calculates movementVector depending on the pressed keys (= keys in the keyPressedArray) */
      function handleMovement() {
        // If no keys are pressed, no event is sent to the backend
        if (keyPressedArray.length === 0) {
          return;
        }

        let forward = new THREE.Vector3(0, 0, 0);
        forward = camera.getWorldDirection(forward);
        forward.y = 0;
        forward.normalize();
        let vector = new THREE.Vector3(0, 0, 0);
        //const angle = Math.PI / 2;
        const rotationAxis = new THREE.Vector3(0, 1, 0);

        if (keyPressedArray.includes('w')) {
          vector = vector.add(forward.clone());
        }

        if (keyPressedArray.includes('a')) {
          let linksVector = new THREE.Vector3(forward.z, 0, -forward.x);
          vector = vector.add(linksVector.normalize());
        }

        if (keyPressedArray.includes('s')) {
          vector = vector.add(forward.clone().negate());
        }

        if (keyPressedArray.includes('d')) {
          let rechtsVector = new THREE.Vector3(-forward.z, 0, forward.x);
          vector = vector.add(rechtsVector.normalize());
        }

        if (keyPressedArray.includes(' ')) {
          vector = vector.add(new THREE.Vector3(0, 1, 0));
        }

        vector.normalize();

        const data = JSON.stringify({
          type: 'MOVE',
          gameID: gameID,
          objectID: userStore.id,
          movementVector: vector,
        });

        sendMessage(data);
      }

      // Calls the handleMovement function in a specified time interval
      setInterval(handleMovement, 25);

      document.addEventListener('mousemove', () => {
        mouseMovement = true;
      });
      // start Render-Loop
      animate();
    }

    function animate() {
      requestAnimationFrame(animate);
      rotateBody();
      const time = Date.now() * 0.001;

      // Update NameTag Orientation
      nameTags.forEach((nameTag) => {
        nameTag.update(player);
      });

      //update all animations
      animationMixers.forEach((mixer) => {
        mixer.update(0.01); 
      }); 

      // Animates food objects, has to loop over entire group at the moments --> better option avaible if performance sucks
      foodGroup.children.forEach((element, index) => {
        element.rotation.y += 0.01;
        element.position.y = (Math.sin(time * 2 + index) * 0.1) + 0.1 * mapScale;
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
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, 0, smoothingFactor);

            // Player body facing forward-right
          } else if (
            forward.z < 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 4, smoothingFactor);

            // Player body facing right
          } else if (angleYCameraDirection < -0.375) {
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 2, smoothingFactor);

            // Player body facing backwards-right
          } else if (
            forward.z > 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
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
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI, smoothingFactor);

            // Player body facing backwards-left
          } else if (
            forward.z > 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
            cone.rotation.z = THREE.MathUtils.lerp(
              currentAngle,
              Math.PI / 2 + Math.PI / 4,
              smoothingFactor,
            );

            // Player body facing left
          } else if (angleYCameraDirection > 0.375) {
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 2, smoothingFactor);

            // Player body facing forward-left
          } else if (
            forward.z < 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
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
