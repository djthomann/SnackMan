import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import * as THREE from 'three'
import bananaModelUrl from '@/assets/models/banana.glb'
import appleModelUrl from '@/assets/models/apple.glb'
import orangeModelUrl from '@/assets/models/orange.glb'
import cakeModelUrl from '@/assets/models/cake.glb'
import chickenModelUrl from '@/assets/models/chicken.glb'
import brokkoliModelUrl from '@/assets/models/brokkoli.glb'

class ModelService {
  private loader: GLTFLoader
  private models: Record<string, string> // Key -> Modell-URLs
  private cache: Map<string, { scene: THREE.Group, animations: THREE.AnimationClip[] }> // Store both scene and animations
  private isInitialized: boolean

  constructor() {
    this.loader = new GLTFLoader()
    this.models = {
      banana: bananaModelUrl,
      apple: appleModelUrl,
      orange: orangeModelUrl,
      cake: cakeModelUrl,
      chicken: chickenModelUrl,
      brokkoli: brokkoliModelUrl
    }
    this.cache = new Map()
    this.isInitialized = false
  }

  /**
   * Initialize the service and load model data, store them in cache
   */
  public async initialize(): Promise<void> {
    if (this.isInitialized) return // Avoid reinitializing

    const loadPromises = Object.entries(this.models).map(([name, url]) =>
      this.loadModel(url).then((modelData) => {
        this.cache.set(name, modelData) // Store scene and animations in cache
      }),
    )

    await Promise.all(loadPromises)
    this.isInitialized = true
    console.log('ModelService initialized: All models loaded.')
  }

  /**
   * Method to retrieve a THREE.Group (Model) from the key string
   * @returns THREE.Group i.e. model
   */
  public getModel(name: string): THREE.Group {
    if (!this.isInitialized) {
      throw new Error('ModelService is not initialized. Call initialize() first.')
    }

    const model = this.cache.get(name)
    if (!model) {
      throw new Error(`Model "${name}" not found in cache.`)
    }
    return model.scene
  }

  /**
   * Method to retrieve animations for a given model
   * @returns Array of AnimationClips
   */
  public getAnimations(name: string): THREE.AnimationClip[] {
    if (!this.isInitialized) {
      throw new Error('ModelService is not initialized. Call initialize() first.')
    }

    const animationData = this.cache.get(name)
    if (!animationData) {
      console.log('No animation data found')
      throw new Error(`Model "${name}" not found in cache.`)
    } else {
      console.log('Animation Found!', animationData.animations)
    }
    return animationData?.animations || []
  }

  /**
   * Tries to load a model from a URL
   */
  private loadModel(url: string): Promise<{ scene: THREE.Group, animations: THREE.AnimationClip[] }> {
    return new Promise((resolve, reject) => {
      this.loader.load(
        url,
        (gltf) => {
          if (url === chickenModelUrl) {
            console.log('GLTF Data:', gltf)
            console.log('Animations:', gltf.animations)
          }
          
          const scene = gltf.scene
          const animations = gltf.animations || []
          
          // Store both the scene and animations in the cache
          resolve({ scene, animations })
        },
        undefined,
        (error) => {
          console.error(`Error loading model from ${url}:`, error)
          reject(error)
        },
      )
    })
  }
}

// Singleton instance
const modelService = new ModelService()
modelService.initialize()
export default modelService
