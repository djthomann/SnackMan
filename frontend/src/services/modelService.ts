import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import * as THREE from 'three';
import bananaModelUrl from '@/assets/models/banana.glb';
import appleModelUrl from '@/assets/models/apple.glb';
import orangeModelUrl from '@/assets/models/orange.glb';
import cakeModelUrl from '@/assets/models/cake.glb';
import chickenModelUrl from '@/assets/models/chicken.glb';
import brokkoliModelUrl from '@/assets/models/brokkoli.glb';
import skybox_dnURL from '@/assets/images/skybox/floor.png';

import { Logger } from '../util/logger';

class ModelService {
  private loader: GLTFLoader;
  private models: Record<string, string>; // Key -> Modell-URLs
  private scales: Record<string, number>;
  private modelCache: Map<string, { scene: THREE.Group }>; // Store scene
  private animationCache: Map<string, { animations: THREE.AnimationClip[] }>;
  private isInitialized: boolean;
  private logger: Logger;
  private texture_dn: THREE.Texture;

  constructor() {
    this.logger = new Logger();
    this.loader = new GLTFLoader();
    this.models = {
      banana: bananaModelUrl,
      apple: appleModelUrl,
      orange: orangeModelUrl,
      cake: cakeModelUrl,
      chicken: chickenModelUrl,
      brokkoli: brokkoliModelUrl,
    };
    this.scales = {
      banana: 0.02,
      apple: 0.1,
      orange: 0.0025,
      cake: 0.175,
      chicken: 1,
      brokkoli: 1,
    };
    this.modelCache = new Map();
    this.animationCache = new Map();
    this.isInitialized = false;
    this.texture_dn = new THREE.TextureLoader().load(skybox_dnURL);
  }

  private scaleModels(globalScale: number): void {
    // Iteriere über alle Einträge der modelCache Map
    for (const [name, modelData] of this.modelCache) {
      // Hole die Skalierung für das aktuelle Modell aus der scales Map
      const scale = this.scales[name] * globalScale;

      // Wenn eine Skalierung definiert ist, wende sie auf das Modell an
      if (scale !== undefined) {
        modelData.scene.scale.set(scale, scale, scale);
      } else {
        console.warn(`Keine Skalierung für Modell "${name}" gefunden.`);
      }
    }
  }

  /**
   * Initialize the service and load model data, store them in cache, scale them
   */
  public async initialize(scale: number): Promise<void> {
    if (this.isInitialized) return; // Avoid reinitializing

    const loadPromises = Object.entries(this.models).map(([name, url]) =>
      this.loadModel(url).then((modelData) => {
        this.modelCache.set(name, modelData); // Store scene in cache
        if (modelData.animations.length > 0) {
          this.animationCache.set(name, modelData);
          console.log('Animation added to Cache');
        }
      }),
    );

    await Promise.all(loadPromises);
    this.scaleModels(scale);
    this.isInitialized = true;
    this.logger.info('ModelService initialized: All models loaded.');
  }

  /**
   * Method to retrieve a THREE.Group (Model) from the key string
   * @returns THREE.Group i.e. model
   */
  public getModel(name: string): THREE.Group {
    if (!this.isInitialized) {
      throw new Error('ModelService is not initialized. Call initialize() first.');
    }

    const model = this.modelCache.get(name);
    if (!model) {
      throw new Error(`Model "${name}" not found in cache.`);
    }
    return model.scene;
  }

  /**
   * Method to retrieve animations for a given model
   * @returns Array of AnimationClips
   */
  public getAnimations(name: string): THREE.AnimationClip[] {
    if (!this.isInitialized) {
      throw new Error('ModelService is not initialized. Call initialize() first.');
    }

    const animationData = this.animationCache.get(name);
    if (!animationData) {
      this.logger.info('No animation data found');
      throw new Error(`Model "${name}" not found in cache.`);
    } else {
      this.logger.info('Animation Found!', animationData.animations);
    }
    return animationData?.animations || [];
  }

  /**
   * Tries to load a model from a URL
   */
  private loadModel(
    url: string,
  ): Promise<{ scene: THREE.Group; animations: THREE.AnimationClip[] }> {
    return new Promise((resolve, reject) => {
      this.loader.load(
        url,
        (gltf) => {
          if (url === chickenModelUrl) {
            this.logger.info('GLTF Data:', gltf);
            this.logger.info('Animations:', gltf.animations);
            this.animationCache.set('chicken', { animations: gltf.animations });
          }

          const scene = gltf.scene;
          const animations = gltf.animations || [];

          // Store both the scene and animations in the cache
          resolve({ scene, animations });
        },
        undefined,
        (error) => {
          console.error(`Error loading model from ${url}:`, error);
          reject(error);
        },
      );
    });
  }

  // Creates small floor tiles
  public createFloorTile(x: number, z: number, scale: number) {
    const planeGeometry = new THREE.PlaneGeometry(scale, scale, 1, 1);
    const planeMaterial = new THREE.MeshStandardMaterial({
      map: this.texture_dn,
    });
    const plane = new THREE.Mesh(planeGeometry, planeMaterial);
    plane.rotation.x = -Math.PI / 2;
    plane.position.set(x * scale, -1, z * scale);
    plane.receiveShadow = true;

    return plane;
  }

  // Creates one cube per wall tile
  public createWall(x: number, z: number, scale: number, wallHeight: number) {
    const boxGeometry = new THREE.BoxGeometry(1 * scale, wallHeight, 1 * scale);
    const boxMaterial = new THREE.MeshToonMaterial({ color: 0x4f4f4f });
    const box = new THREE.Mesh(boxGeometry, boxMaterial);
    box.position.set(x * scale, 0, z * scale);
    box.castShadow = true;

    return box;
  }

  // Creates Food item, chooses model depending on calories --> randomnly generated in frontend right now (not good)
  public createFood(id: number, x: number, z: number, calories: number, scale: number) {
    let newModel;
    if (calories > 300) {
      newModel = this.getModel('brokkoli').clone();
    } else if (calories > 200) {
      newModel = this.getModel('apple').clone();
    } else {
      newModel = this.getModel('cake').clone();
    }
    newModel.userData.id = id;
    newModel.position.set(x * scale, 10, z * scale);
    return newModel;
  }

  public createChicken(x: number, z: number) {
    const chickenModel = this.getModel('chicken');
    this.logger.info('ChickenModel loaded');
    const chicken = chickenModel.clone();
    chicken.castShadow = true;
    chicken.scale.set(5, 5, 5);
    chicken.position.set(x, 0, z);
    this.logger.info('Chicken at:', chicken.position);
    chicken.rotation.y = -45;
    return chicken;
  }
}

// Singleton instance
const modelService = new ModelService();
export default modelService;
