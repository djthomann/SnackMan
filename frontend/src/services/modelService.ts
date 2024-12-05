import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import * as THREE from 'three'
import bananaModelUrl from '@/assets/models/banana.glb'
import appleModelUrl from '@/assets/models/apple.glb'
import orangeModelUrl from '@/assets/models/orange.glb'
import cakeModelUrl from '@/assets/models/cake.glb'

class ModelService {
  private loader: GLTFLoader
  private models: Record<string, string> // Key -> Modell-URLs
  private cache: Map<string, THREE.Group> // Key -> ThreeJS
  private isInitialized: boolean

  constructor() {
    this.loader = new GLTFLoader()
    this.models = {
      banana: bananaModelUrl,
      apple: appleModelUrl,
      orange: orangeModelUrl,
      cake: cakeModelUrl,
    }
    this.cache = new Map()
    this.isInitialized = false
  }

  /**
   * Initialize the service and load model data, store them in cache
   * 
   */
  public async initialize(): Promise<void> {
    if (this.isInitialized) return // Doppelte Initialisierung vermeiden

    const loadPromises = Object.entries(this.models).map(([name, url]) =>
      this.loadModel(url).then((model) => {
        this.cache.set(name, model) // store model in cache
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
    return model
  }

  /**
   * Tries to load a model from a URL
   */
  private loadModel(url: string): Promise<THREE.Group> {
    return new Promise((resolve, reject) => {
      this.loader.load(
        url,
        (gltf) => {
          const scene = gltf.scene

          resolve(scene)
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
