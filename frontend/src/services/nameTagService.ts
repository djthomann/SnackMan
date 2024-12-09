import * as THREE from 'three';


class NameTag{

    private nameTagObj: THREE.Mesh;
    private parentObject: THREE.Mesh;

    constructor(name: string, parentObject: THREE.Mesh, scene: THREE.Scene){
        

        // Create Canvas
        const canvas = document.createElement('canvas');
        canvas.width = 256;
        canvas.height = 128;

        // Create Context
        const context = canvas.getContext('2d');

        //Check if Context was generated 
        if (!context) {
            console.error('Failed to get 2D context.');
            return;
        }else{
            console.log('Success getting 2D context')
        }

        // Write and Style Name
        context.fillStyle = 'white'
        context.font = '60px sans-serif';
        context.fillText(name, 0, 60);

        // Create Texture and Material
        const texture = new THREE.Texture(canvas);
        texture.needsUpdate = true;
        const usernameMaterial = new THREE.MeshBasicMaterial({
            map: texture,
            side: THREE.DoubleSide,
        })
        usernameMaterial.transparent = true;
        
        // Create NameTag Object
        const nameTagObj = new THREE.Mesh(new THREE.PlaneGeometry(1, 0.3), usernameMaterial);
        scene.add(nameTagObj);

        // Position NameTag above ParentObject (Player)
        nameTagObj.position.set(parentObject.position.x, parentObject.position.y + 0.5, parentObject.position.z);

        // Add NameTag to Parent
        parentObject.add(nameTagObj);

        // Save reference for updates
        this.nameTagObj = nameTagObj;
        this.parentObject = parentObject;
    }

    update(player: THREE.Mesh){
        this.nameTagObj.lookAt(player.position);
    }

    
}

export default NameTag;