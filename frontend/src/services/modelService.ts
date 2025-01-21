import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import * as THREE from 'three';
import bananaModelUrl from '@/assets/models/banana.glb';
import appleModelUrl from '@/assets/models/apple.glb';
import cheeseModelUrl from '@/assets/models/cheese.glb';
import orangeModelUrl from '@/assets/models/orange.glb';
import cakeModelUrl from '@/assets/models/cake.glb';
import chickenModelUrl from '@/assets/models/chicken.glb';
import brokkoliModelUrl from '@/assets/models/brokkoli.glb';
import snackmanModelUrl from '@/assets/models/snackmouse.glb';
import ghostModelUrl from '@/assets/models/ghost.glb';
import floorURL from '@/assets/images/skybox/floor.png';
import playerModelUrl from '@/assets/models/player.glb';
import skybox_ftURL from '@/assets/images/skybox/skybox_ft.png';
import skybox_bkURL from '@/assets/images/skybox/skybox_bk.png';
import skybox_upURL from '@/assets/images/skybox/skybox_up.png';
import skybox_dnURL from '@/assets/images/skybox/skybox_dn.png';
import skybox_lfURL from '@/assets/images/skybox/skybox_lf.png';
import skybox_rtURL from '@/assets/images/skybox/skybox_rt.png';
import counter_dnURL from '@/assets/images/counter/counter_down.png';
import counter_lfURL from '@/assets/images/counter/counter_left.png';
import counter_rtURL from '@/assets/images/counter/counter_right.png';
import counter_ftURL from '@/assets/images/counter/counter_front.png';
import counter_bkURL from '@/assets/images/counter/counter_back.png';
import counter_top1URL from '@/assets/images/counter/counter_top1.png';
import counter_top2URL from '@/assets/images/counter/counter_top2.png';
import counter_top3URL from '@/assets/images/counter/counter_top3.png';
import counter_top4URL from '@/assets/images/counter/counter_top4.png';
import counter_top5URL from '@/assets/images/counter/counter_top5.png';
import snackman_blueURL from '@/assets/models/snackman_blue.glb';
import snackman_redURL from '@/assets/models/snackman_red.glb';
import snackman_greenURL from '@/assets/models/snackman_green.glb';
import snackman_yellowURL from '@/assets/models/snackman_yellow.glb';



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
  private skyBoxTextures: THREE.MeshBasicMaterial[];
  private counterTextures: THREE.MeshBasicMaterial[];
  private topTextures: THREE.Texture[];
  private modelVariants: string[];

  constructor() {
    this.logger = new Logger();
    this.loader = new GLTFLoader();
    this.models = {
      banana: bananaModelUrl,
      apple: appleModelUrl,
      cheese: cheeseModelUrl,
      orange: orangeModelUrl,
      cake: cakeModelUrl,
      chicken: chickenModelUrl,
      brokkoli: brokkoliModelUrl,
      snackman: snackmanModelUrl,
      snackman_blue: snackman_blueURL,
      snackman_red: snackman_redURL,
      snackman_green: snackman_greenURL,
      snackman_yellow: snackman_yellowURL,
      player: playerModelUrl,
      ghost: ghostModelUrl
    };
    this.scales = {
      banana: 0.02,
      apple: 0.1,
      cheese: 0.12,
      orange: 0.0025,
      cake: 0.175,
      chicken: 1,
      brokkoli: 1,
      snackman: 0.12,
      snackman_blue: 0.12,
      snackman_red: 0.12,
      snackman_green: 0.12,
      snackman_yellow: 0.12,
      player: 0.12,
      ghost: 0.14
    };
    this.modelCache = new Map();
    this.animationCache = new Map();
    this.isInitialized = false;
    this.texture_dn = new THREE.TextureLoader().load(floorURL);
    this.modelVariants = ['snackman_blue', 'snackman_red', 'snackman_green', 'snackman_yellow'];


    this.skyBoxTextures = [
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_ftURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_bkURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_upURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_dnURL), transparent: true, opacity: 0}),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_rtURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(skybox_lfURL) })
    ]

    this.counterTextures = [
      new THREE.MeshBasicMaterial({ map: this.loadTexture(counter_ftURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(counter_bkURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(counter_dnURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(counter_rtURL) }),
      new THREE.MeshBasicMaterial({ map: this.loadTexture(counter_lfURL) }),
  ];

   this.topTextures = [
      counter_top1URL,
      counter_top2URL,
      counter_top3URL,
      counter_top4URL,
      counter_top5URL,
  ].map(url => this.loadTexture(url));

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
          this.logger.info('Animation added to Cache');
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

  // Helper Function for Loading Textures in the SRGB ColorSpace
  private loadTexture(url: string): THREE.Texture {
    const texture = new THREE.TextureLoader().load(url);
    texture.colorSpace = THREE.SRGBColorSpace;
    return texture;
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


  public createSkybox(w: number){

    // Skybox
    for (let i = 0; i < 6; i++) {
      this.skyBoxTextures[i].side = THREE.BackSide;
      this.skyBoxTextures[i].transparent = true;
    }
    const skyboxGeo = new THREE.BoxGeometry(w, w/4, w)
    const skybox = new THREE.Mesh(skyboxGeo, this.skyBoxTextures);
    skybox.name = "skybox";
    skybox.position.y = w/4/2;
    skybox.position.x = w/2;
    skybox.position.z = w/2;

    return skybox;
  }

  // Creates small floor tiles
  public createFloorTile(x: number, z: number, scale: number) {
    const planeGeometry = new THREE.PlaneGeometry(scale, scale, 1, 1);
    const planeMaterial = new THREE.MeshStandardMaterial({
      map: this.texture_dn,
    });
    const plane = new THREE.Mesh(planeGeometry, planeMaterial);
    plane.rotation.x = -Math.PI / 2;
    plane.position.set((x + 0.5) * scale, 0, (z + 0.5) * scale);
    plane.receiveShadow = true;

    return plane;
  }

  // Create Textured Walls
  public createWall(x: number, z: number, scale: number, wallHeight: number): THREE.Mesh {
    // Random top texture
    const counterTextureUp = new THREE.MeshBasicMaterial({
        map: this.topTextures[Math.floor(Math.random() * this.topTextures.length)],
    });

    // UV-Mapping
    const materials = [
        this.counterTextures[0], // Front
        this.counterTextures[1], // Back
        counterTextureUp,        // Top - Random
        this.counterTextures[2], // Bottom
        this.counterTextures[3], // Right
        this.counterTextures[4], // Left
    ];

    const boxGeometry = new THREE.BoxGeometry(1 * scale, wallHeight, 1 * scale);
    const box = new THREE.Mesh(boxGeometry, materials);

    box.position.set((x + 0.5) * scale, 0.5 * scale, (z + 0.5) * scale);
    box.castShadow = true;

    // Random Rotation
    box.rotation.y = Math.floor(Math.random() * 4) * Math.PI / 2;

    return box;
  }


  public createPlayer(id: number | undefined, x: number, y: number,  z: number) {
    const res = new THREE.Group();
    const newModel = this.getModel('player').clone();
    newModel.userData.id = id;
    res.position.set(x,y,z);
    newModel.name = "model";
    res.add(newModel);
    return res;
  }

  //Creates Snackman and positions it
  public createSnackman(id: number, x: number, y: number, z: number) {
    if (this.modelVariants.length === 0) {
        this.modelVariants = ['snackman_blue', 'snackman_red', 'snackman_green', 'snackman_yellow'];
    }

    // Assign a model from the list
    const modelName = this.modelVariants.pop() as string;
    const newModel = this.getModel(modelName).clone();

    newModel.userData.id = id;
    newModel.position.set(x, 0, z);

    return newModel;
}



  public createGhostPlayer(id: number | undefined, x: number, y: number,  z: number) {
    const res = new THREE.Group();
    const newModel = this.getModel('ghost').clone();
    newModel.userData.id = id;
    res.position.set(x,y,z);
    newModel.name = "model";
    res.add(newModel);
    return res;
  }

    //Creates Ghost and positions it
  public createGhost(id: number, x: number, y: number, z: number) {
    const newModel = this.getModel('ghost').clone();
    newModel.userData.id = id;
    newModel.position.set(x, 0, z);
    return newModel;
  }
  // Creates Food item, chooses model depending on calories --> randomnly generated in frontend right now (not good)
  public createFood(id: number, x: number, z: number, calories: number, scale: number) {
    let newModel;
    if (calories > 300) {
      newModel = this.getModel('brokkoli').clone();
    } else if (calories > 200) {
      newModel = this.getModel('cheese').clone();
    } else {
      newModel = this.getModel('cake').clone();
    }
    newModel.userData.id = id;
    newModel.position.set((x + 0.5) * scale, 0.5, (z + 0.5) * scale);
    return newModel;
  }

  public createChicken(id: number, x: number, z: number) {
    const chickenModel = this.getModel('chicken');
    this.logger.info('ChickenModel loaded');
    chickenModel.userData.id = id;
    const chicken = chickenModel.clone();
    chicken.castShadow = true;
    chicken.scale.set(3.25, 3, 3);     // Radius 0.1 -> (3.25,3,3), Radius 0.2 -> (6.5,6,6), Radius 0.5 -> (15.25, 15, 15)
    chicken.position.set(x, 0, z);
    this.logger.info('Chicken at:', chicken.position);
    chicken.rotation.y = 0; // Degrees * Math.PI / 180
    return chicken;
  }
}

// Singleton instance
const modelService = new ModelService();
export default modelService;
