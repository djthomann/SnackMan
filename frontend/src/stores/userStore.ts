import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const username = ref<string>('');
  const id = ref<number>();
  const role = ref<string>('');

  const setUsername = (newUsername: string) => {
    username.value = newUsername;
  };

  const setId = (newId: number) => {
    id.value = newId;
  };

  const setRole = (newRole: string) => {
    role.value = newRole;
  }

  return { username, setUsername, id, setId , role, setRole};
});