<template>
  <div>
    <p>This is the home page for an amazing plane that is everywhere at once.</p>
    <button @click="goToFlightSelection">Go to Search</button>
  </div>
</template>

<script>
import axios from "axios";
import { useRouter } from "vue-router";
export default {
  setup() {
    const router = useRouter();
    const goToFlightSelection = async () => {
      try {
        const response = await axios.post("/api/start", null);
        const planeId = response.data;
        await router.push({
          path: "/flights",
          query: {
            planeId: planeId,
          },
        });
      } catch (error) {
        console.error("API query failed:", error);
      }
    };
    return { goToFlightSelection };
  },
};
</script>

