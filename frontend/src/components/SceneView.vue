<template>
  <div ref="rendererContainer" class="canvas-container">
    <Transition name="fade" mode="out-in">
      <LoadingOverlayComponent v-if="isLoading"></LoadingOverlayComponent>
    </Transition>
    <GameOverlay ref="gameOverlayRef" />
  </div>
  <div id="clickable-container"></div>
</template>

<script setup lang="ts">
import { defineComponent, onUnmounted, ref, onMounted, nextTick } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';
import * as THREE from 'three';
import { PointerLockControls } from 'three/examples/jsm/Addons.js';
import modelService from '@/services/modelService';
import type { Ghost, Snackman, Chicken, Food } from '@/types/SceneTypes';
import { useEntityStore } from '@/stores/entityStore';
import { useGameStore } from '@/stores/gameStore';
import { storeToRefs } from 'pinia';
import NameTag from '@/services/nameTagService';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { Mesh } from 'three';
import GameOverlay from './GameOverlay.vue';
import { Logger } from '../util/logger';
import LoadingOverlayComponent from './layout/LoadingOverlayComponent.vue';
import SoundService, { SoundEffect, type SoundParameters } from '@/services/soundService';

// Groups of different map objects
let wallsGroup: THREE.Group;
let floorGroup: THREE.Group;
let foodGroup: THREE.Group;
let chickenGroup: THREE.Group;

// mesh for walls
let box: THREE.Mesh;

const mapScale = 5;
const wallHeight = 1 * mapScale;


    // Background music and soundeffects
    const listener = new THREE.AudioListener();
    const soundService = new SoundService(listener);
    

    const gameOverlayRef = ref<InstanceType<typeof GameOverlay> | null>(null);
    const isLoading = ref<boolean>(true);
    

    const { sendMessage } = useWebSocket();

    const rendererContainer = ref<HTMLDivElement | null>(null);
    const serverMessage = ref<string>('');
    const route = useRoute();
    const userStore = useUserStore();
    let renderer: THREE.WebGLRenderer;
    let camera: THREE.PerspectiveCamera;
    let scene: THREE.Scene;
    let plane: THREE.Mesh;
    let ambientLight: THREE.AmbientLight;
    let directionalLight: THREE.DirectionalLight;
    let controls: PointerLockControls;
    let mouseMovement = false;
    let nameTag: NameTag;
    const animationMixers: THREE.AnimationMixer[] = [];
    const nameTags: NameTag[] = [];
    let gameID = 2;
    let startPromiseResolve: () => void;
    let lastRotation: THREE.Vector3 = new THREE.Vector3();

    const logger = new Logger();

    // Sound stuff
    /* const sounds = [];
    let foodSound1: THREE.Audio, foodSound2: THREE.Audio, collisionSound: THREE.Audio;
    const manager = new THREE.LoadingManager();
    manager.onLoad = () => console.log('sounds loaded');
    const audioLoader = new THREE.AudioLoader(manager);
    const mp3s = ['eat1', 'eat2', 'hit'];
    const listener = new THREE.AudioListener();
    mp3s.forEach((name) => {
      const sound = new THREE.Audio(listener);
      sound.name = name;
      if (name === 'eat1') {
        foodSound1 = sound;
        sound.setVolume(0.5);
      }
      if (name === 'eat2') {
        foodSound2 = sound;
        sound.setVolume(0.5);
      }
      if (name === 'hit') {
        collisionSound = sound;
        sound.setVolume(0.5);
      }
      sounds.push(sound);
      audioLoader.load(`src/assets/sounds/soundeffects/${name}.mp3`, function (buffer) {
        sound.setBuffer(buffer);
      });
    }); */

    //GameStart
    const entityStore = useEntityStore();
    const gameStore = useGameStore();
    const { snackMen, ghosts, chickens, map } = storeToRefs(entityStore);
    const meshes: Map<number, THREE.Group> = new Map<number, THREE.Group>();

    logger.info(
      'Snackman Names:',
      [...snackMen.value.values()].map((item: Snackman) => item.username),
    );

    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message;

      if (message.startsWith('GAME_START')) {
        handleStartEvent(message.split(';')[1]);
        if (startPromiseResolve) {
          startPromiseResolve();
        }
      } else if (message.startsWith('GAME_STATE')) {
        if(isLoading.value){
          isLoading.value = false;
        }
        handleGameStateEvent(message.split(';')[1]);
      } else {
        logger.warn(`FE does not support message starting with ${message.split(";")[0]}`)
      }
    };

    const handleGameStateEvent = (message: string) => {

      const parsedData = JSON.parse(message);
      gameStore.setRemainingTime(parsedData.remainingSeconds);

      parsedData.eatenFoods.forEach((food: Food) => {
        playFoodSound(food.objectId);
        makeDisappear(food.objectId);
      })

      parsedData.laidEggs.forEach((food: Food) => {
        logger.info(`Egg gets laid`)
        makeAppear(food)
      });

      parsedData.updatesSnackMen.forEach((snackman: Snackman) => {
        if(snackman.objectId === userStore.id) {
          gameStore.setCalories(snackman.gainedCalories);
        }
        meshes
          .get(snackman.objectId)!
          .position.set(snackman.x * mapScale, snackman.y, snackman.z * mapScale);
      });

      parsedData.updatesGhosts.forEach((ghost: Ghost) => {
        if(ghost.objectId === userStore.id) {
          gameStore.setCollisions(ghost.collisions)
        }
        meshes.get(ghost.objectId)!.position.set(ghost.x * mapScale, ghost.y * mapScale, ghost.z * mapScale);
      });

      parsedData.updatesChickens.forEach((chicken: Chicken) => {
          resizeChicken(chicken.objectId, chicken.radius);
          move(chicken.objectId, chicken.x, chicken.y, chicken.z);
      });
    };
    const handleStartEvent = (message: string) => {
      logger.info('handle start event');

      const parsedData = JSON.parse(message);
      gameStore.setRemainingTime(parsedData.gameTime);

      parsedData.snackMen.forEach((snackman: Snackman) => {

      if(snackman.objectId === userStore.id) {
        gameStore.setCalories(snackman.gainedCalories);
      }

      });
      loadMap(map.value);
    };

    const waitForStartMessage = () => {
      return new Promise<void>((resolve) => {
        startPromiseResolve = resolve;
      });
    };

    function resizeChicken(id: number, radius: number) {
      // logger.info('chicken with radius: ' + radius);

      chickenGroup.children.forEach((chicken) => {
        if(chicken.userData.id === id) {
          // Update scale
          chicken.scale.set(
            radius * 32.5,
            radius * 32,
            radius * 32
          );
        }
      })
    }

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
          )

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

      loadChickens([...chickens.value.values()]);
      loadPlayerEntities([...snackMen.value.values()], [...ghosts.value.values()], scene);

      window.addEventListener('resize', onWindowResize);

      const lastSegment = route.path.split('/').pop() || '';
      if (/^\d+$/.test(lastSegment)) {
        gameID = Number(lastSegment);
      }

      // start Render-loop
      animate()
      logger.info('scene with gameID ' + gameID);
    });

    onUnmounted(() => {
      eventBus.off('serverMessage', handleServerMessage);

      // Three.js Cleanup
      controls?.dispose();
      renderer.dispose();
      if (rendererContainer.value && renderer.domElement) {
        rendererContainer.value.removeChild(renderer.domElement);
      }

      // Stop sounds
      soundService.stopBackgroundMusic();
      soundService.stopSound(chickenGroup);

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

    function loadChickens(chickens: Chicken[]) {
      chickens.forEach(newChicken => {
        const chicken = modelService.createChicken(newChicken.objectId, newChicken.x * mapScale, newChicken.z * mapScale);

        chickenGroup.add(chicken);

        const chickenMixer = new THREE.AnimationMixer(chicken);
        const chickenAnimations = modelService.getAnimations('chicken');

        if (chickenAnimations.length > 0) {
          const action = chickenMixer.clipAction(chickenAnimations[0]);
          action.play();
        } else {
          logger.error('Animation not found');
        }

        // Mixer is stored in a global list, so that it can be used in the update-loop
        animationMixers.push(chickenMixer);
      });

      scene.add(chickenGroup);

      // Load chicken sound for each chicken
      const chickenSoundParams: SoundParameters = {autoplaying: true, looping: true, volume: 5};
      soundService.addPositionalAudio(SoundEffect.CHICKEN, chickenGroup, chickenSoundParams);
    }

    /*
      Ghosts and Snackmen are spawned on the correct position
    */

    function loadPlayerEntities(snackMen: Snackman[], ghosts: Ghost[], scene: any) {
      // Group for snackMen and ghosts
      const snackMenGroup = new THREE.Group();
      snackMenGroup.name = "snackmen"
      const ghostsGroup = new THREE.Group();
      ghostsGroup.name = "ghosts";

      // Iterate over snackMen and add them to the scene
      snackMen.forEach((snackMan) => {

        if (snackMan.objectId == userStore.id) {
          const playerMesh = modelService.createPlayer(userStore.id ,snackMan.x * mapScale, snackMan.y, snackMan.z * mapScale);
          playerMesh.add(camera)
          camera.position.set(0, mapScale / 2, 0);
          playerMesh.add(controls.object);
          meshes.set(snackMan.objectId, playerMesh);
          scene.add(playerMesh);
        } else {
          const snackManMesh = modelService.createSnackman(snackMan.objectId, snackMan.x * mapScale, snackMan.y * mapScale, snackMan.z * mapScale);
          // Attach a NameTag
          const snackManTag = new NameTag(snackMan.username, snackManMesh, scene);
          nameTags.push(snackManTag);
          // Add to snackMen group
          scene.add(snackManMesh);
          meshes.set(snackMan.objectId, snackManMesh);
        }
      });

      // Iterate over ghosts and add them to the scene
      ghosts.forEach((ghost) => {

        if (ghost.objectId == userStore.id) {
          const ghostMesh = modelService.createGhostPlayer(ghost.objectId, ghost.x * mapScale, ghost.y * mapScale, ghost.z * mapScale);
          ghostMesh.visible = false;
          ghostMesh.add(camera)
          camera.position.set(0, mapScale, 0);
          ghostMesh.add(controls.object);
          meshes.set(ghost.objectId, ghostMesh);
          scene.add(ghostMesh);
        } else {
          const ghostMesh = modelService.createGhost(ghost.objectId, ghost.x * mapScale, ghost.y * mapScale, ghost.z * mapScale);
          const ghostTag = new NameTag(ghost.username || 'Ghost', ghostMesh, scene);
          nameTags.push(ghostTag);
          // Add to ghosts group
          scene.add(ghostMesh);
          meshes.set(ghost.objectId, ghostMesh);
        }
      });
      // Add groups to the scene
      // scene.add(snackMenGroup);
      // scene.add(ghostsGroup);
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
            const food = modelService.createFood(tile.food.objectId, tile.x, tile.z, tile.food.calories, mapScale);
            food.userData.id = tile.food.objectId;
            foodGroup.add(food);
            floorGroup.add(modelService.createFloorTile(tile.x, tile.z, mapScale));
          } else if (occupationType == 'FREE') {
            floorGroup.add(modelService.createFloorTile(tile.x, tile.z, mapScale));
          }
        }
      }

      scene.add(wallsGroup);
      scene.add(foodGroup);
      scene.add(floorGroup);

      // Add eating sound
      const params: SoundParameters = {autoplaying: false, looping: false, volume: 2, refDistance: 30, rolloff: 0.5, maxDistance: 50};
      soundService.addPositionalAudio(SoundEffect.EAT, foodGroup, params);

      const skyBox = modelService.createSkybox(w);
      scene.add(skyBox);
    }

    function makeDisappear(id: number) {
      foodGroup.children.forEach((food) => {
        if (food.userData.id == id) {
          scene.remove(food);
          foodGroup.remove(food);
          console.log(`food with Id ${id} disappeared juhu`);
        }
      });
    }

    function makeAppear(newFood: Food) {
      const food = modelService.createFood(newFood.objectId, newFood.x, newFood.z, newFood.calories, mapScale);
      food.userData.id = newFood.objectId;
      foodGroup.add(food);
      scene.remove(foodGroup)
      scene.add(foodGroup)
    }

    function initScene() {


      // Scene
      scene = new THREE.Scene();
      scene.background = new THREE.Color(0x111111);

      wallsGroup = new THREE.Group();
      floorGroup = new THREE.Group();
      foodGroup = new THREE.Group();
      chickenGroup = new THREE.Group();
      wallsGroup.name = "walls";
      floorGroup.name = "floor";
      foodGroup.name = "food";
      chickenGroup.name = "chicken";

      // Camera
      camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, mapScale * 1000);
      camera.position.set(0, 0, 0)
      camera.getWorldDirection(lastRotation);
      camera.add(listener);
      // camera.lookAt(1, 1, 1);

      // Background music
      soundService.startBackgroundMusic();

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

      // TODO: For testing, take out later
      //const ghostGeomatry = new THREE.CylinderGeometry(0.35 * mapScale, 0.35 * mapScale, 0.75 * mapScale);
      //const ghostMaterial = new THREE.MeshToonMaterial({ color: 0xff0000 });
      //const ghostMesh = new THREE.Mesh(ghostGeomatry, ghostMaterial);
      //ghostMesh.position.set(16.5 * mapScale, 0, 20.5 * mapScale)
      //scene.add(ghostMesh)

      // Dummy Cylinder in the Center to test the Measurements of the Models.
      //const geometry = new THREE.CylinderGeometry(0.2 * mapScale, 0.2 * mapScale, 3, 32);
      //const material = new THREE.MeshStandardMaterial({ color: 0x00ff00 });
      //const cylinder = new THREE.Mesh(geometry, material);
      //cylinder.position.set( 20.5 * mapScale , 0, 20.5 * mapScale );
      //scene.add(cylinder);

      // PointerLock Controls
      controls = new PointerLockControls(camera, renderer.domElement);
      const clickableContainer = document.getElementById('clickable-container') as HTMLInputElement;
      clickableContainer.addEventListener(
        'click',
        function () {
          controls.lock();
        },
        false,
      );

      // Hide and unhide start button
      controls.addEventListener('lock', () => {
      });

      controls.addEventListener('unlock', () => {
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
          const linksVector = new THREE.Vector3(forward.z, 0, -forward.x);
          vector = vector.add(linksVector.normalize());
        }

        if (keyPressedArray.includes('s')) {
          vector = vector.add(forward.clone().negate());
        }

        if (keyPressedArray.includes('d')) {
          const rechtsVector = new THREE.Vector3(-forward.z, 0, forward.x);
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
    }

    function animate() {
      requestAnimationFrame(animate);

      rotateBody();
      const time = Date.now() * 0.001;

      // Update NameTag Orientation
      nameTags.forEach((nameTag) => {
        nameTag.update(meshes.get(userStore.id!)!);
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

      meshes.get(userStore.id!)!.getObjectByName("model")!.getWorldDirection(playerForward);
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
          const currentAngle = meshes.get(userStore.id!)!.rotation.y;

          // Player body facing forward
          if (forward.z < 0 && angleYCameraDirection < 0.125 && angleYCameraDirection > -0.125) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, 0, smoothingFactor);

            // Player body facing forward-right
          } else if (
            forward.z < 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, -Math.PI / 4, smoothingFactor);

            // Player body facing right
          } else if (angleYCameraDirection < -0.375) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, -Math.PI / 2, smoothingFactor);

            // Player body facing backwards-right
          } else if (
            forward.z > 0 &&
            angleYCameraDirection < -0.125 &&
            angleYCameraDirection > -0.375
          ) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(
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
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, Math.PI, smoothingFactor);

            // Player body facing backwards-left
          } else if (
            forward.z > 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(
              currentAngle,
              Math.PI / 2 + Math.PI / 4,
              smoothingFactor,
            );

            // Player body facing left
          } else if (angleYCameraDirection > 0.375) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, Math.PI / 2, smoothingFactor);

            // Player body facing forward-left
          } else if (
            forward.z < 0 &&
            angleYCameraDirection > 0.125 &&
            angleYCameraDirection < 0.375
          ) {
            meshes.get(userStore.id!)!.getObjectByName("model")!.rotation.y = THREE.MathUtils.lerp(currentAngle, Math.PI / 4, smoothingFactor);
          }
        }
      }
    }

    function playFoodSound(id: number) {
      foodGroup.children.forEach((food) => {
        if (food.userData.id == id) {
          soundService.playSound(food);
        }
      });
    }

</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

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

#clickable-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: 0;
  opacity: 0;
  z-index: 2;
}
</style>
