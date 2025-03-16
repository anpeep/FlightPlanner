<template>
  <v-container fluid class="home-container d-flex justify-center align-center">
    <div class="animation-container">
      <!-- Inspiration: https://codepen.io/Decomind/pen/KKPxJpZ -->
      <div class="plane">
        <img src="https://images.vexels.com/media/users/3/145795/isolated/preview/05cd33059a006bf49006097af4ccba98-plane-in-flight-by-vexels.png" alt="plane taking off"/>
      </div>
      <div class="text-button-container">
        <h2 class="welcome-text">Welcome to the home of the first plane that can be everywhere at once!</h2>
        <v-btn class="main-button" @click="goToFlightSelection">Go to Search</v-btn>
      </div>
    </div>
  </v-container>
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
          query: { planeId },
        });
      } catch (error) {
        console.error("API query failed:", error);
      }
    };
    return { goToFlightSelection };
  },
};
</script>

<style scoped>
.home-container {
  height: 100vh;
  background: linear-gradient(to bottom, #6FA1FF, #A3CDFF);
  position: relative;
  overflow: hidden;
}

.animation-container {
  width: 100%;
  height: 100%;
}

.text-button-container {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
}

.welcome-text {
  font-size: 1.5rem;
  color: white;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
  margin-bottom: 10px;
}

.main-button {
  background-color: #87BFFF;
  color: white;
}

</style>

.plane {
position: absolute;
width: 80px;
opacity: 0.9;
animation: takeoff 8s linear;
}
@keyframes takeoff {
0% {
transform: translate(-10vw, 100vh) rotate(0deg);
}
25% {
transform: translate(-20vw, 80vh) rotate(5deg);
}
50% {
transform: translate(40vw, 50vh) rotate(10deg);
}
75% {
transform: translate(60vw, 20vh) rotate(5deg);
}
100% {
transform: translate(80vw, -10vh) rotate(10deg);
opacity: 0;
}
}
