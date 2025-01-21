<template>
  <div>
    <BackgroundComponent :title="`LOBBY #${lobbyCode}`">
      <FetchMap></FetchMap>
      <div class="icon-button add-button button volume-button">
            <button @click="toggleMute">
              <img v-if="volume > 0" src="@/assets/icons/volume.svg" />
              <img v-if="volume == 0" src="@/assets/icons/volume_muted.svg" />
            </button>
            <input v-model="volume" type="range" id="volume-slider" name="volume" min="0" max="1" step="0.01" />
          </div>
      <div class="lobby-grid">
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="ghost" :selected="selectedRole === 'GHOST'">
            <template #counter>{{ ghostPlayers.length }}/4</template>
            <template #button
              ><button class="chooseRoleButton" type="button" @click="decide(false)">
                Ghost
              </button></template
            >
            <template #content>
              <li v-for="player in ghostPlayers" :key="player.id" class="player-list-item">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <ConfigPanelComponent
            headline="Game Settings:"
            :counter="{ current: undecidedPlayers.length, max: 8 }"
          >
            <template #content>
              <div class="config-panel-grid">
                <FieldsetComponent height-behaviour="stretch">
                  <span>Slay</span>
                  <span>Slay</span>
                </FieldsetComponent>
                <FieldsetComponent height-behaviour="stretch">
                  <form
                    @submit.prevent="submitForm"
                    id="gameConfig"
                    :action="`/lobby/${lobbyCode}`"
                    method="POST"
                  >
                    <span>
                      <label for="scoreToWin">Score to Win:</label>
                      <input
                        type="number"
                        id="scoreToWin"
                        v-model="gameConfig.scoreToWin"
                        @input="validateField('scoreToWin')"
                      />
                      <p v-if="errors.scoreToWin" class="error-message">{{ errors.scoreToWin }}</p>
                    </span>
                    <span>
                      <label for="speedModifier">Movement-Speed:</label>
                      <input
                        type="number"
                        id="speedModifier"
                        v-model="gameConfig.speedModifier"
                        @input="validateField('speedModifier')"
                      />
                      <p v-if="errors.speedModifier" class="error-message">
                        {{ errors.speedModifier }}
                      </p>
                    </span>
                    <span>
                      <label for="snackmanSpeed">Snackman Speed:</label>
                      <input
                        type="number"
                        id="snackmanSpeed"
                        v-model="gameConfig.snackManSpeed"
                        @input="validateField('snackManSpeed')"
                      />
                      <p v-if="errors.snackManSpeed" class="error-message">
                        {{ errors.snackManSpeed }}
                      </p>
                    </span>
                    <span>
                      <label for="ghostSpeed">Ghost Speed:</label>
                      <input
                        type="number"
                        id="ghostSpeed"
                        v-model="gameConfig.ghostSpeed"
                        @input="validateField('ghostSpeed')"
                      />
                      <p v-if="errors.ghostSpeed" class="error-message">{{ errors.ghostSpeed }}</p>
                    </span>
                    <span>
                      <label for="chickenSpeed">Chicken Speed:</label>
                      <input
                        type="number"
                        id="chickenSpeed"
                        v-model="gameConfig.chickenSpeed"
                        @input="validateField('chickenSpeed')"
                      />
                      <p v-if="errors.chickenSpeed" class="error-message">
                        {{ errors.chickenSpeed }}
                      </p>
                    </span>
                    <span>
                      <label for="mapWidth">Map Width in tiles:</label>
                      <input
                        type="number"
                        id="mapWidth"
                        v-model="gameConfig.mapWidth"
                        @input="validateField('mapWidth')"
                      />
                      <p v-if="errors.mapWidth" class="error-message">{{ errors.mapWidth }}</p>
                    </span>
                    <span>
                      <label for="mapHeight">Map Height in tiles:</label>
                      <input
                        type="number"
                        id="mapHeight"
                        v-model="gameConfig.mapHeight"
                        @input="validateField('mapHeight')"
                      />
                      <p v-if="errors.mapHeight" class="error-message">{{ errors.mapHeight }}</p>
                    </span>
                    <span>
                      <label for="gameTime">Game Time in seconds:</label>
                      <input
                        type="number"
                        id="gameTime"
                        v-model="gameConfig.gameTime"
                        @input="validateField('gameTime')"
                      />
                      <p v-if="errors.gameTime" class="error-message">{{ errors.gameTime }}</p>
                    </span>
                    <span>
                      <label for="chickenCount">Number of Chicken:</label>
                      <input
                        type="number"
                        id="chickenCount"
                        v-model="gameConfig.chickenCount"
                        @input="validateField('chickenCount')"
                      />
                      <p v-if="errors.chickenCount" class="error-message">
                        {{ errors.chickenCount }}
                      </p>
                    </span>
                    <span>
                      <label for="jumpCalories">Calories Burned on Jump:</label>
                      <input
                        type="number"
                        id="jumpCalories"
                        v-model="gameConfig.jumpCalories"
                        @input="validateField('jumpCalories')"
                      />
                      <p v-if="errors.jumpCalories" class="error-message">
                        {{ errors.jumpCalories }}
                      </p>
                    </span>
                    <button type="submit" :disabled="hasErrors">Apply</button>
                    <button type="button" @click="resetForm">Reset</button>
                  </form>
                </FieldsetComponent>
              </div>
            </template>
          </ConfigPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="snackman" :selected="selectedRole === 'SNACKMAN'">
            <template #counter>{{ snackManPlayers.length }}/4</template>
            <template #button
              ><button class="chooseRoleButton" type="button" @click="decide(true)">
                SnackMan
              </button></template
            >
            <template #content>
              <li v-for="player in snackManPlayers" :key="player.id" class="player-list-item">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column lobby-grid__column--span-all">
          
          <button class="startGameButton" type="button" @click="startGame">Start Game</button>
        </div>
      
      </div>
    </BackgroundComponent>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import { useUserStore } from '@/stores/userStore';
import type { Player } from '@/types/SceneTypes';
import BackgroundComponent from './layout/BackgroundComponent.vue';
import PlayerPanelComponent from './layout/PlayerPanelComponent.vue';
import { Logger } from '../util/logger';
import ConfigPanelComponent from './layout/ConfigPanelComponent.vue';
import FieldsetComponent from './layout/FieldsetComponent.vue';
import FetchMap from '@/components/FetchMap.vue';

// TODO find and replace with similar free music because these songs are copyrighted
import lobbyMusic from '@/assets/sounds/music/Lobby-music.mp3';

const logger = new Logger();

const { sendMessage } = useWebSocket();
const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));
const serverMessage = ref<string>('');
const userStore = useUserStore();
const name = computed(() => userStore.username);
const clientID = computed(() => userStore.id);
const snackManPlayers = ref<Array<Player>>([]);
const ghostPlayers = ref<Array<Player>>([]);
const undecidedPlayers = ref<Array<Player>>([
  { id: 1, username: 'Alice' },
  { id: 2, username: 'Bob' },
]);
const selectedRole = ref<'SNACKMAN' | 'GHOST' | null>(null);
const audio = new Audio(lobbyMusic);
let oldVolume: number = 1
let muted: boolean = false
const volume = ref<number>(1)
const volumeIcon = "@/assets/icons/volume.svg";
const mutedIcon = "@/assets/icons/mute.svg";

// Type definition of GameConfig interface
interface GameConfig {
  scoreToWin: number | null;
  speedModifier: number | null;
  snackManSpeed: number | null;
  ghostSpeed: number | null;
  chickenSpeed: number | null;
  mapWidth: number | null;
  mapHeight: number | null;
  gameTime: number | null;
  chickenCount: number | null;
  jumpCalories: number | null;
}
// Reactive variable to store game config
const gameConfig = ref<GameConfig>({
  scoreToWin: null,
  speedModifier: null,
  snackManSpeed: null,
  ghostSpeed: null,
  chickenSpeed: null,
  mapWidth: null,
  mapHeight: null,
  gameTime: null,
  chickenCount: null,
  jumpCalories: null,
});

// Method, to get GameConfig from BE
const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('GAME_CONFIG')) {
    gameConfig.value = JSON.parse(message.split(';')[1]);
  } else if (message.startsWith('GAME_START')) {
    //TODO: get this event into scene
    router.push('/game/' + lobbyCode.value);
  } else if (message.startsWith('PLAYERS')) {
    buildPlayerArrays(message);
  } else if (message.startsWith('FOREIGN_GAMESTART')) {
    router.push('/game/' + lobbyCode.value);
  }
};

const buildPlayerArrays = (message: string) => {
  const parsedData = JSON.parse(message.split(';')[1]);
  const snackmanArray: Array<Player> = [];
  const ghostArray: Array<Player> = [];
  const undecidedArray: Array<Player> = [];
  parsedData.players.forEach((player: string[]) => {
    if (Number(player[1]) == userStore.id) {
      userStore.setRole(player[2]);
    }
    console.log(`Username: ${player[0]}, Client ID: ${player[1]}`);
    if (player[2] === 'SNACKMAN') {
      snackmanArray.push({ id: Number(player[1]), username: player[0] });
    } else if (player[2] === 'GHOST') {
      ghostArray.push({ id: Number(player[1]), username: player[0] });
    } else {
      undecidedArray.push({ id: Number(player[1]), username: player[0] });
    }
  });
  snackManPlayers.value = snackmanArray;
  undecidedPlayers.value = undecidedArray;
  ghostPlayers.value = ghostArray;
};

const errors = ref<Record<string, string>>({});

const hasErrors = computed(() => {
  return Object.values(errors.value).some((error) => error !== '');
});

// Produces error message of config validation
const validateField = (field: string) => {
  const value = gameConfig.value[field as keyof GameConfig];
  let error = '';

  switch (field) {
    case 'snackManSpeed':
    case 'ghostSpeed':
    case 'chickenSpeed':
    case 'mapWidth':
    case 'mapHeight':
    case 'gameTime':
    case 'jumpCalories':
    case 'speedModifier':
      if (!value || value <= 0) {
        error = 'Value must be greater than 0.';
      }
      break;

    case 'scoreToWin':
      if (!value || value < 1100) {
        error = 'Value must be greater than 1100.';
      }
      break;

    case 'chickenCount':
      if (!value || value < 0 || value > 4) {
        error = 'Value must be between 0 and 4.';
      }
      break;

    default:
      break;
  }

  errors.value[field] = error;
};

// Method, to send GameConfig to BE as JSON
const submitForm = async () => {
  Object.keys(gameConfig.value).forEach(validateField);

  if (!hasErrors.value) {
    const data = JSON.stringify({
      type: 'SET_GAME_CONFIG',
      gameID: lobbyCode.value,
      objectID: 0,
      gameConfig: gameConfig.value,
    });
    sendMessage(data);
  }
};

// On Reset-Button press, send message to BE with gameID: 0, to signify default value request
const resetForm = async () => {
  const reset = JSON.stringify({
    type: 'RESET_GAME_CONFIG',
    gameID: lobbyCode.value,
  });
  sendMessage(reset);
};

const decide = (snackman: boolean) => {
  selectedRole.value = snackman ? 'SNACKMAN' : 'GHOST';
  const data = JSON.stringify({
    type: 'ROLE',
    snackman: snackman,
    id: userStore.id,
  });
  sendMessage(data);

  fetchPlayers();
};

const toggleMute = () => {
  if(muted) {
    muted = false
    volume.value = oldVolume;
  } else {
    muted = true
    oldVolume = volume.value
    volume.value = 0;
  }
}

// Automatic call on load
onMounted(async () => {

  // Start background music on load of the lobby view
  audio.play();

  // Method, to wait until Connection is established
  const awaitConnection = () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve('Connected');
      }, 1000);
    });
  };

  watch(volume, (newValue, oldValue) => {
    audio.volume = volume.value;
  });

  //Stops the background music when leaving the lobby view
  onBeforeUnmount(() => {
    audio.pause();
  });

  // Registration of EventBus
  eventBus.on('serverMessage', handleServerMessage);

  // Sending request for GameConfig to BE
  try {
    await awaitConnection();
    const requestData = JSON.stringify({
      type: 'GET_GAME_CONFIG',
      gameID: lobbyCode.value,
    });
    sendMessage(requestData);
  } catch (e) {
    logger.info('Failed to fetch Data on load: ', e);
  }

  decide(true); // initial role Snackman
  fetchPlayers();
});

const fetchPlayers = () => {
  const requestData = JSON.stringify({
    type: 'GET_PLAYERS',
    lobbyCode: lobbyCode.value,
  });
  sendMessage(requestData);
};

// Method, to start the game
const startGame = () => {
  const requestData = JSON.stringify({
    type: 'START_GAME',
    gameID: lobbyCode.value,
  });
  sendMessage(requestData);
  router.push('/game/' + lobbyCode.value);
};

const choose = (role: string) => {
  const message = JSON.stringify({
    type: 'CHOOSEROLE',
    clientID: clientID.value,
    username: name.value,
    gameID: lobbyCode.value,
    isSnackMan: role === 'SnackMan' ? true : false,
  });
  sendMessage(message);
};

</script>

<style scoped>
.lobby-grid {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-rows: 1fr auto;
  gap: 40px 60px;
  padding: 4dvw;
  box-sizing: border-box;
}

.lobby-grid__column {
  min-height: 0;
}

.lobby-grid__column--span-all {
  grid-column: 1/4;
  text-align: center;
}

.config-panel-grid {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 100%;
  gap: 30px;
}

.startGameButton {
  width: 25%;
  background-color: #f39325;
  color: white;
  border: 3px solid white;
  border-radius: 10px;
}

.chooseRoleButton {
  width: 100%;
  background-color: white;
  color: #ff8c00;
  border: none;
}

Button {
  padding: 10px;
  font-size: 1.5rem;
  font-weight: bold;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

Button:hover {
  color: black;
  transform: scale(1.05);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.startGameButton:hover {
  background-color: white;
  border: 3px solid black;
}

input[type='number'] {
  appearance: none;
  -moz-appearance: textfield;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px;
  width: 100%;
  box-sizing: border-box;
}

input[type='number']::-webkit-outer-spin-button,
input[type='number']::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

label {
  color: white;
}

.player-list-item {
  list-style-type: none;
  text-align: center;
  padding: 5px 0;
}

.error-message {
  margin-top: 4px;
  padding: 0px 5px;
}

#volume-slider {
  opacity: 0;
  width: 0;
  transition: none;
}

.volume-button {
  position: absolute;
  bottom: 2%;
  left: 0;
  width: auto;
  display: flex;
  align-items: center;
}

.button {
  user-select: none;
  margin-left: 1%;
}

.icon-button img {
  height: 40px;
  transition: transform 0.5s ease;
  user-select: none;
}

.icon-button button {
  display: flex;
  background: 0;
  box-shadow: none;
  border: 0;
  user-select: none;
}

.icon-button button:hover {
  cursor: pointer;
  transform: scale(1.1);
}

.volume-button:hover #volume-slider {
  animation: expandVolumeSlider 0.2s forwards;
}

.volume-button:not(:hover) #volume-slider {
  animation: collapseVolumeSlider 0.2s forwards;
}

@keyframes expandVolumeSlider {
  0% {
    width: 0;
    opacity: 0;
  }
  100% {
    width: 100px;
    opacity: 1;
  }
}

@keyframes collapseVolumeSlider {
  0% {
    width: 100px;
    opacity: 1;
  }
  100% {
    width: 0;
    opacity: 0;
  }
}
</style>
