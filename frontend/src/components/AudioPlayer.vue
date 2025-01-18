<!-- AudioPlayer.vue -->
<template>
    <div></div>
    <!-- Hier wird keine sichtbare Darstellung benötigt -->
  </template>
  
  <script>
  import * as THREE from 'three';
  
  export default {
    name: 'AudioPlayer',
    props: {
      audioFile: {
        type: String,
        required: true,
      },
      loop: {
        type: Boolean,
        default: false,
      },
      volume: {
        type: Number,
        default: 0.5,
      },
    },
    data() {
      return {
        sound: null,
        listener: null,
      };
    },
    mounted() {
      this.initAudio();
    },
    methods: {
      initAudio() {

        this.listener = new THREE.AudioListener();
        this.sound = new THREE.Audio(this.listener);
  
        // Lade die Audio-Datei
        const audioLoader = new THREE.AudioLoader();
        audioLoader.load(this.audioFile, (buffer) => {
          this.sound.setBuffer(buffer);
          this.sound.setLoop(this.loop); // Loop-Option basierend auf der Eingabe
          this.sound.setVolume(this.volume); // Lautstärke einstellen
          this.sound.play();
        });
      },
      play() {
        if (this.sound) {
          this.sound.play();
        }
      },
      stop() {
        if (this.sound) {
          this.sound.stop();
        }
      },
      setVolume(volume) {
        if (this.sound) {
          this.sound.setVolume(volume);
        }
      },
    },
    destroyed() {
      if (this.sound) {
        this.sound.stop();
      }
    },
  };
  </script>
  