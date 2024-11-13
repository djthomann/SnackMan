<template>
    <div ref="rendererContainer" class="canvas-container"></div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import * as THREE from 'three';
  import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
  
  const rendererContainer = ref<HTMLDivElement | null>(null);
  let renderer: THREE.WebGLRenderer;
  let camera: THREE.PerspectiveCamera;
  let scene: THREE.Scene;
  let box: THREE.Mesh;
  let plane: THREE.Mesh;
  let ambientLight: THREE.AmbientLight;
  let directionalLight: THREE.DirectionalLight;
  let controls: OrbitControls | null = null;
  
  function initScene() {
    // Szene erstellen
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x111111);
  
    // Kamera erstellen
    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    camera.position.set(5, 7.5, 7.5);
  
    // Renderer erstellen
    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.shadowMap.enabled = true;
    
    // Füge den Renderer zur Komponente hinzu
    rendererContainer.value?.appendChild(renderer.domElement);
  
    // Box erstellen
    const boxGeometry = new THREE.BoxGeometry(1.5, 1.5, 1.5);
    const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
    box = new THREE.Mesh(boxGeometry, boxMaterial);
    box.position.set(0, 0, 0);
    box.castShadow = true;
    scene.add(box);
  
    // Plane erstellen
    const planeGeometry = new THREE.PlaneGeometry(20, 20, 20, 20);
    const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xf7f7f7 });
    plane = new THREE.Mesh(planeGeometry, planeMaterial);
    plane.rotation.x = -Math.PI / 2;
    plane.position.set(0, -3, 0);
    plane.receiveShadow = true;
    scene.add(plane);
  
    // Ambient Light hinzufügen
    ambientLight = new THREE.AmbientLight(0xffffff, 1);
    scene.add(ambientLight);
  
    // Directional Light hinzufügen
    directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(0, 2, 0);
    directionalLight.castShadow = true;
    scene.add(directionalLight);
  
    // Orbit Controls (optional)
    controls = new OrbitControls(camera, renderer.domElement);
  
    // Render-Loop starten
    animate();
  }
  
  function animate(time = 0) {
    requestAnimationFrame(animate);
  
    // Box-Rotation
    const delta = time * 0.0000002;
    if (box) {
      box.rotation.y += delta;
      box.rotation.z = delta;
    }
  
    controls?.update();
    renderer.render(scene, camera);
  }
  
  // Cleanup bei Entfernen der Komponente
  onBeforeUnmount(() => {
    controls?.dispose();
    renderer.dispose();
    rendererContainer.value?.removeChild(renderer.domElement);
  });
  
  onMounted(() => {
    initScene();
  
    // Resize-Event
    window.addEventListener('resize', onWindowResize);
  });
  
  function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  }
  </script>
  
  <style scoped>
  html {
    margin: 0;
    padding: 0;
  }
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
  