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

export default defineComponent({
  name: 'Scene',
  setup() {

    const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket();

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

    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message;
      console.log("Processing server message");

      if (message.startsWith("MOVE")) {
        let key: string = message.split(":")[1]

        if (key === "KeyD") {
          cone.position.x += 0.2;
        } else if (key === "KeyA") {
          cone.position.x -= 0.2;
        } else if (key === "KeyW") {
          cone.position.z -= 0.2;
        } else if (key === "KeyS") {
          cone.position.z += 0.2;
        }
      }
    };


    onMounted(() => {
      initScene();
      eventBus.on('serverMessage', handleServerMessage);


      connect();

      window.addEventListener('resize', onWindowResize);
    });

    onUnmounted(() => {
      eventBus.off('serverMessage', handleServerMessage);

      closeConnection();

      // Three.js Cleanup
      controls?.dispose();
      renderer.dispose();
      if (rendererContainer.value && renderer.domElement) {
        rendererContainer.value.removeChild(renderer.domElement);
      }

      window.removeEventListener('resize', onWindowResize);
    });

    function initScene() {
      // Scene
      scene = new THREE.Scene();
      scene.background = new THREE.Color(0x111111);

      // Camera
      camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
      camera.position.set(0, 1.7, 0);


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

      // Ground Plane
      const planeGeometry = new THREE.PlaneGeometry(20, 20, 20, 20);
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 });
      plane = new THREE.Mesh(planeGeometry, planeMaterial);
      plane.rotation.x = -Math.PI / 2;
      plane.position.set(0, -3, 0);
      plane.receiveShadow = true;
      scene.add(plane);

      // Ambient Light
      ambientLight = new THREE.AmbientLight(0xffffff, 1);
      scene.add(ambientLight);

      // Directional Light
      directionalLight = new THREE.DirectionalLight(0xffffff, 1);
      directionalLight.position.set(0, 2, 0);
      directionalLight.castShadow = true;
      scene.add(directionalLight);

      // Player Object
      scene.add(player);
      player.add(camera);
      player.add(cone);

      // Player Body
      const coneGeometry = new THREE.ConeGeometry(0.5, 1, 32);
      const coneMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
      cone = new THREE.Mesh(coneGeometry, coneMaterial);
      cone.position.set(0, 0.5, 0);
      cone.rotation.x = -Math.PI / 2;
      cone.castShadow = true;
      scene.add(cone);

      // PointerLock Controls
      controls = new PointerLockControls(camera, renderer.domElement);
      const startButton = document.getElementById("startButton") as HTMLInputElement;
      player.add(controls.object);
      startButton.addEventListener(
        'click',
        function () {
          controls.lock();
        },
        false
      )

      // Hide and unhide start button
      controls.addEventListener('lock', () => {
        startButton.classList.add('hidden'); // Button verstecken
      });

      controls.addEventListener('unlock', () => {
        startButton.classList.remove('hidden'); // Button anzeigen
      });

      // Handle key press und send Event via WebSocket
      const handleKeyPress = (event: KeyboardEvent) => {
        if (['w', 'a', 's', 'd'].includes(event.key)) {
          let vector = new THREE.Vector3();
          const angle = Math.PI / 2;
          const rotationAxis = new THREE.Vector3(0, 1, 0);

          // Calculate movement vector
          switch (event.key) {
            case 'w':
              vector = forward.clone();
              break;
            case 'a':
              vector = forward.clone().applyAxisAngle(rotationAxis, angle).normalize();
              break;
            case 's':
              vector = forward.clone().negate();
              break;
            case 'd':
              vector = forward.clone().applyAxisAngle(rotationAxis, angle).normalize()
              break;
          }
          //TODO: give vector to sendMessage()
          sendMessage(`KEY:${event.code}`);
          //console.log('MovementVector:', vector);
        }
      };

      document.addEventListener('keypress', handleKeyPress);
      document.addEventListener('mousemove', () => {
        mouseMovement = true;
      })

      // start Render-Loop
      animate();
    }

    function animate() {
      requestAnimationFrame(animate);
      rotateBody();
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
            (playerForward.x - forward.x),
            (playerForward.z - forward.z)
          );

          // Interpolation for smooth rotation
          const smoothingFactor = 0.1;
          const currentAngle = cone.rotation.z;

          // Player body facing forward
          if (forward.z < 0 && angleYCameraDirection < 0.125 && angleYCameraDirection > -0.125) {
            //console.log('Face Forward');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, 0, smoothingFactor);

            // Player body facing forward-right
          } else if (forward.z < 0 && angleYCameraDirection < -0.125 && angleYCameraDirection > -0.375) {
            //console.log('Turn Forward Right');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 4, smoothingFactor);

            // Player body facing right
          } else if (angleYCameraDirection < -0.375) {
            //console.log('Turn Right');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 2, smoothingFactor);

            // Player body facing backwards-right
          } else if (forward.z > 0 && angleYCameraDirection < -0.125 && angleYCameraDirection > -0.375) {
            //console.log('Turn Backward Right');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, -Math.PI / 2 - Math.PI / 4, smoothingFactor);

            // Player body facing backwards
          } else if (forward.z > 0 && angleYCameraDirection < 0.125 && angleYCameraDirection > -0.125) {
            //console.log('Turn Backwards');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI, smoothingFactor);

            // Player body facing backwards-left
          } else if (forward.z > 0 && angleYCameraDirection > 0.125 && angleYCameraDirection < 0.375) {
            //console.log('Turn Backward Left');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 2 + Math.PI / 4, smoothingFactor);

            // Player body facing left
          } else if (angleYCameraDirection > 0.375) {
            //console.log('Turn Left');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 2, smoothingFactor);

            // Player body facing forward-left
          } else if (forward.z < 0 && angleYCameraDirection > 0.125 && angleYCameraDirection < 0.375) {
            //console.log('Turn Forward Left');
            cone.rotation.z = THREE.MathUtils.lerp(currentAngle, Math.PI / 4, smoothingFactor);

          }
        }
      }
    }

    return {
      rendererContainer,
      serverMessage
    };
  }
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
