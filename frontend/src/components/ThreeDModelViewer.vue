<script lang="ts">
// Optional ModelViewer Component that can be used to view a model in the frontend if necessary
import { defineComponent, onMounted } from 'vue';
import * as THREE from 'three';
import modelService from '@/services/modelService';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

export default defineComponent({
  name: 'ThreeDModelViewer',
  setup() {
    let scene: THREE.Scene;
    let camera: THREE.PerspectiveCamera;
    let renderer: THREE.WebGLRenderer;
    let controls: OrbitControls;

    onMounted(async () => {
      // Szene initialisieren
      scene = new THREE.Scene();
      camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
      renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setSize(window.innerWidth, window.innerHeight);
      document.body.appendChild(renderer.domElement);

      controls = new OrbitControls(camera, renderer.domElement);

      // Kamera positionieren
      camera.position.set(0, 5, 10);
      camera.lookAt(0, 0, 0);

      // Licht hinzufügen
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
      scene.add(ambientLight);

      const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
      directionalLight.position.set(5, 10, 7.5);
      scene.add(directionalLight);

      try {
        // Service initialisieren
        await modelService.initialize();

        // Modell abrufen und zur Szene hinzufügen
        const bananaModel = modelService.getModel('banana');
        bananaModel.scale.set(2, 2, 2); // Modell skalieren
        bananaModel.position.set(0, 0, 0); // Modell positionieren
        scene.add(bananaModel);
        console.log('Model added to scene:', bananaModel);
      } catch (error) {
        console.error('Error initializing or loading model:', error);
      }

      // Animation starten
      function animate() {
        requestAnimationFrame(animate);
        renderer.render(scene, camera);
      }
      animate();
    });
  },
});
</script>
