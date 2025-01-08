import * as THREE from 'three';
import { Logger } from '../util/logger';

class NameTag {
  private nameTagObj: THREE.Mesh | undefined = undefined;
  private logger: Logger;

  constructor(name: string, parentObject: THREE.Group, scene: THREE.Scene) {
    this.logger = new Logger();

        // Create Canvas
        const canvas = document.createElement('canvas');
        canvas.width = 500;
        canvas.height = 128;

    // Create Context
    const context = canvas.getContext('2d');

    //Check if Context was generated
    if (!context) {
      console.error('Failed to get 2D context.');
      return;
    } else {
      this.logger.info('Success getting 2D context');
    }

    // Write and Style Name
    context.fillStyle = 'white';
    context.font = '60px sans-serif';
    context.fillText(name, 0, 60);

        // Create Texture and Material
        const texture = new THREE.Texture(canvas);
        texture.needsUpdate = true;
        const usernameMaterial = new THREE.MeshBasicMaterial({
            map: texture,
            side: THREE.DoubleSide,
            depthWrite: false
        })
        usernameMaterial.transparent = true;
        
        // Create NameTag Object
        const nameTagObj = new THREE.Mesh(new THREE.PlaneGeometry(1, 0.3), usernameMaterial);
        scene.add(nameTagObj);

        // Position NameTag above ParentObject (Player)
        nameTagObj.position.set(0, 0, 0); // Relative to parent object's center
        nameTagObj.position.y += parentObject.position.y/2;

    // Add NameTag to Parent
    parentObject.add(nameTagObj);

    // Save reference for updates
    this.nameTagObj = nameTagObj;
  }

  update(player: THREE.Mesh) {
    this.nameTagObj?.lookAt(player.position);
  }
}

export default NameTag;
