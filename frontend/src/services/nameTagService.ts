import * as THREE from 'three';
import { Logger } from '../util/logger';

class NameTag {
  private nameTagObj: THREE.Mesh | undefined = undefined;
  private logger: Logger;

  constructor(name: string, parentObject: THREE.Group, scene: THREE.Scene, scale: number) {
    this.logger = new Logger();

    // Create Canvas
    const baseWidth = 1800;
    const baseHeight = 450;
    const canvas = document.createElement('canvas');
    canvas.width = baseWidth * scale;
    canvas.height = baseHeight * scale;

    // Create Context
    const context = canvas.getContext('2d');

    // Check if Context was generated
    if (!context) {
      console.error('Failed to get 2D context.');
      return;
    } else {
      this.logger.info('Success getting 2D context');
    }

    // Write and Style Name
    context.fillStyle = 'black';
    context.font = `bold ${90 * scale}px sans-serif`;
    context.textAlign = 'center'; 
    context.textBaseline = 'middle'; 

    const textX = canvas.width / 2;
    const textY = canvas.height / 2;

    context.fillText(name, textX, textY);

    // Create Texture and Material
    const texture = new THREE.Texture(canvas);
    texture.needsUpdate = true;

    const usernameMaterial = new THREE.MeshBasicMaterial({
      map: texture,
      side: THREE.DoubleSide,
      depthWrite: false,
      transparent: true
    });

    const width = 1.5 * scale; 
    const height = 0.5 * scale;
    const nameTagObj = new THREE.Mesh(new THREE.PlaneGeometry(width, height), usernameMaterial);
    
    scene.add(nameTagObj);

    // Position NameTag above ParentObject (Player)
    nameTagObj.position.set(0, scale * 1.6, 0);

    // Add NameTag to Parent
    parentObject.add(nameTagObj);

    // Save reference for updates
    this.nameTagObj = nameTagObj;
  }

  update(player: THREE.Group) {
    this.nameTagObj?.quaternion.copy(player.quaternion);
    //this.nameTagObj?.lookAt(player.position);

  }
}

export default NameTag;
