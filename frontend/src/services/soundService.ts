import * as THREE from 'three';
import { Logger } from '../util/logger';

// TODO find and replace gameMusic with similar free music because this song is copyrighted
import gameMusic from '@/assets/sounds/music/Game-music.mp3';
import chickenCackle from '@/assets/sounds/soundeffects/chicken.mp3';
import eatSound from '@/assets/sounds/soundeffects/eat1.mp3';

export type SoundParameters = {
    autoplaying: boolean;
    looping: boolean;
    volume: number;
    refDistance?: number;
    rolloff?: number;
    maxDistance?: number;
}

export enum SoundEffect {
    EAT,
    CHICKEN,
    JUMP
  }

class SoundService {

    
    private listener: THREE.AudioListener;
    private audioLoader: THREE.AudioLoader;
    private backgroundSound: THREE.Audio<GainNode>;
    private soundEffects: Map<SoundEffect, string>;
    private logger: Logger;
 

    constructor(listener: THREE.AudioListener) {
        this.listener = listener;
        this.backgroundSound = new THREE.Audio(listener);
        this.audioLoader = new THREE.AudioLoader();
        // Map to store all sound effects
        this.soundEffects = new Map<SoundEffect, string>([
            [SoundEffect.EAT, eatSound],
            [SoundEffect.CHICKEN, chickenCackle],
            [SoundEffect.JUMP, gameMusic],  // TODO replace with jump sound
        ]);
        this.logger = new Logger();
    }

    // Adds a positional audio to a 3D object or group of objects
    addPositionalAudio(soundType: SoundEffect, soundSource: THREE.Object3D<THREE.Object3DEventMap> | THREE.Group<THREE.Object3DEventMap>, params: SoundParameters) {

        const sound = this.soundEffects.get(soundType);

        if(!sound) {
            this.logger.error("Sound not found");
            return;
        }

        if (soundSource instanceof THREE.Group) {

            this.audioLoader.load(sound, (buffer) => {
            soundSource.children.forEach(item => {
                this.addAudioToItem(item, buffer, params);
                });
            });
        
        } else if (soundSource instanceof THREE.Object3D) {

            this.audioLoader.load(sound, (buffer) => {
                this.addAudioToItem(soundSource, buffer, params);
            });
        }
    }

    private addAudioToItem(item: THREE.Object3D, buffer: AudioBuffer, params: SoundParameters) {
        const audio = new THREE.PositionalAudio(this.listener);
        audio.setBuffer(buffer);
        audio.setVolume(params.volume);

        if(params.refDistance) {
            audio.setRefDistance(params.refDistance);
        }
        if(params.rolloff) {
            audio.setRolloffFactor(params.rolloff);
        }
        if(params.maxDistance) {
            audio.setMaxDistance(params.maxDistance);
        }
        if(params.looping) {
            audio.setLoop(true);
        }
        if(params.autoplaying) {
            audio.play();
        }

        item.add(audio);
    }

    // Plays the sound attached to a 3D object
    playSound(mesh: THREE.Object3D) {
        const sound = mesh.children.find(child => child instanceof THREE.PositionalAudio) as THREE.PositionalAudio;
        if (sound) {
          if (!sound.isPlaying) {
            sound.play();
          }
        }
      }

      // Stops the sound for a 3D Object or group of objects
      stopSound(soundSource: THREE.Object3D<THREE.Object3DEventMap> | THREE.Group<THREE.Object3DEventMap>) {
        
        if(soundSource instanceof THREE.Group) {

            // Stop the audio for each child of the group
            soundSource.children.forEach(item => {
                const sound = item.children.find(child => child instanceof THREE.PositionalAudio) as THREE.PositionalAudio;
                if (sound) {
                    sound.stop();
                }
            });

        } else if(soundSource instanceof THREE.Object3D) {
            const sound = soundSource.children.find(child => child instanceof THREE.PositionalAudio) as THREE.PositionalAudio;
            if (sound) {
                sound.stop();
            }
        }
      }

      // Starts the game's background music
      startBackgroundMusic() {
        this.audioLoader.load(gameMusic, (buffer) => {
          this.backgroundSound.setBuffer(buffer);
          this.backgroundSound.setLoop(true);
          this.backgroundSound.setVolume( 0.5 );
          this.backgroundSound.play();
        });
      }

      // Stops the game's background music
      stopBackgroundMusic() {
        this.backgroundSound.stop();
      }
}

export default SoundService;
