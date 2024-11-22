import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import * as THREE from 'three'
import bananaModelUrl from '@/assets/models/banana.glb'
import appleModelUrl from '@/assets/models/apple.glb'
import orangeModelUrl from '@/assets/models/orange.glb'

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
    }
    this.cache = new Map()
    this.isInitialized = false
  }

  /**
   * Initialisiert den Service, indem das Modell vorab geladen wird.
   * @returns Promise<void>
   */
  public async initialize(): Promise<void> {
    if (this.isInitialized) return // Doppelte Initialisierung vermeiden

    const loadPromises = Object.entries(this.models).map(([name, url]) =>
      this.loadModel(url).then((model) => {
        this.cache.set(name, model) // Modell in den Cache hinzufügen
      }),
    )

    await Promise.all(loadPromises)
    this.isInitialized = true
    console.log('ModelService initialized: All models loaded.')
  }

  /**
   * Holt ein Modell anhand seines Namens. Stellt sicher, dass es zuvor geladen wurde.
   * @param name - Der Name des Modells
   * @returns THREE.Group
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
   * Lädt ein Modell von einer gegebenen URL (intern verwendet).
   * @param url - Die URL des Modells
   * @returns Promise<THREE.Group>
   */
  private loadModel(url: string): Promise<THREE.Group> {
    return new Promise((resolve, reject) => {
      this.loader.load(
        url,
        (gltf) => {
          const scene = gltf.scene

          // Berechne die Bounding Box
          const box = new THREE.Box3().setFromObject(scene)
          const center = box.getCenter(new THREE.Vector3())

          // Verschiebe das Modell, um es zu zentrieren
          scene.position.sub(center)

          // Optional: Aktualisiere die Bounding Box nach der Verschiebung
          const updatedBox = new THREE.Box3().setFromObject(scene)

          console.log('Updated Bounding Box:', updatedBox)

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

// Singleton-Instanz des ModelService
const modelService = new ModelService()
modelService.initialize()
export default modelService
