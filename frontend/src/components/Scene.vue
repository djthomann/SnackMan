<template>
  <div ref="rendererContainer" class="canvas-container"></div>
</template>

<script lang="ts">
import { defineComponent, onUnmounted, ref, onMounted } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

export default defineComponent({
  name: 'Scene',
  setup() {

    const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket();

    const rendererContainer = ref<HTMLDivElement | null>(null);
    const serverMessage = ref<string>('');
    let renderer: THREE.WebGLRenderer;
    let camera: THREE.PerspectiveCamera;
    let scene: THREE.Scene;
    let box: THREE.Mesh;
    let plane: THREE.Mesh;
    let ambientLight: THREE.AmbientLight;
    let directionalLight: THREE.DirectionalLight;
    let controls: OrbitControls | null = null;
    let movementVector = new THREE.Vector3();




    // React to server message (right now only simple movement)
    const handleServerMessage = (message: string) => {
      serverMessage.value = message;
      console.log("Processing server message");

      if (message.startsWith("MOVE")) {
        let key: string = message.split(":")[1]

        if (key === "KeyD") {
          box.position.x += 0.2;
        } else if (key === "KeyA") {
          box.position.x -= 0.2;
        } else if (key === "KeyW") {
          box.position.z -= 0.2;
        } else if (key === "KeyS") {
          box.position.z += 0.2;
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
      camera.position.set(5, 7.5, 7.5);

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
      const boxGeometry = new THREE.BoxGeometry(1.5, 1.5, 1.5);
      const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
      box = new THREE.Mesh(boxGeometry, boxMaterial);
      box.position.set(0, 0, 0);
      box.castShadow = true;
      scene.add(box);

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

      // Orbit Controls
      controls = new OrbitControls(camera, renderer.domElement);

      // Handle key press und send Event via WebSocket
      const handleKeyPress = (event: KeyboardEvent) => {
        movementVector = calculateMovement(event.key, forward);
        //TODO: give MovementVector to sendMessage()
        sendMessage(`KEY:${event.code}`);
        console.log(`Key pressed: ${event.key}`);
        console.log(`Movement Vector: ${movementVector}`);
      };

      document.addEventListener('keypress', handleKeyPress);


      // start Render-Loop
      animate();
    }

    function animate() {
      requestAnimationFrame(animate);
      controls?.update();
      renderer.render(scene, camera);
    }

    function onWindowResize() {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    }

    function calculateMovement(key: string, forward: THREE.Vector3) {
      const vector = new THREE.Vector3();
      const angle = Math.PI / 2;
      const rotationMatrix = new THREE.Matrix4();

      switch (key) {
        case 'KeyW':
          return forward;
        case 'KeyA':
          rotationMatrix.makeRotationY(angle);
          vector.applyMatrix4(rotationMatrix);
          break;
        case 'KeyS':
          return -forward;
        case 'KeyD':
          rotationMatrix.makeRotationY(-angle);
          vector.applyMatrix4(rotationMatrix);
          break;
      }
      return vector.normalize();
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
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
