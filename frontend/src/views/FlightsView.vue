<template>
  <div>
    <h1>Lennuplaan</h1>

    <div class="filters">
      <label for="destination">Sihtkoht:</label>
      <input type="text" v-model="filters.arrivalCity" @input="applyFilters" placeholder="Sisesta arrival city" />

      <label for="departure">Departure:</label>
      <input type="text" v-model="filters.departureCity" @input="applyFilters" placeholder="Sisesta departure" />

      <label for="priceRange">Hind (€):</label>
      <div class="price-range-container">
        <div class="price-range">
          <input
              type="range"
              v-model="filters.priceMin"
              :min="minPrice"
              :max="maxPrice"
              :step="0.1"
              @input="applyFilters"
          />
        </div>
        <div class="price-display">
          <span>€{{ filters.priceMin.toFixed(2) }}</span>
        </div>
        <div class="range-labels">
          <span>€{{ minPrice.toFixed(2) }}</span>
          <span>€{{ maxPrice.toFixed(2) }}</span>
        </div>
      </div>

      <label for="dateFrom">Alates kuupäev:</label>
      <input type="date" v-model="filters.dateFrom" @input="applyFilters" />

      <label for="dateTo">Kuni kuupäev:</label>
      <input type="date" v-model="filters.dateTo" @input="applyFilters" />
    </div>

    <div v-if="flights.length > 0">
      <div
          v-for="flight in filteredFlights"
          :key="flight.id"
          class="flight-item"
          @click="goToSeatSelection(flight)"
      >
        <p><strong>Sihtkoht:</strong> {{ flight.arrivalCity }}</p>
        <p><strong>Lennuk lahkub:</strong> {{ formatDate(flight.departOn) }}</p>
        <p><strong>Lennu kestus:</strong> {{ calculateDuration(flight.departOn, flight.arriveOn) }} tundi</p>
        <p><strong>Hind:</strong> €{{ flight.price }}</p>
        <p><strong>Lennuki ID:</strong> {{ planeId }}</p>
        <button @click.stop="goToSeatSelection(flight)">Vali iste</button>
      </div>
    </div>

    <div v-else>
      <p>Laen lennuplaani...</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { useRoute, useRouter } from "vue-router"; // Vue 3 komposiitmeetodid

export default {
  setup() {
    const router = useRouter();
    const route = useRoute(); // Läbime URL-i query parameetrid

    // Kogume 'planeId' URL-i query-st
    const planeId = route.query.planeId;
    console.log("Lennuki ID, mis saadeti eelmiselt lehelt:", planeId);
// Siin saad määrata soovitud piletite arvu

    const goToSeatSelection = (flight) => {
      console.log("honey im home")
      // Kui kasutaja vajutab lennu peale, siis tehakse päring, et saada istmete andmed
      axios.post("/api/seats/getSeats", null, {
          params: {
            flightId: flight.id, // Valitud lennu ID
            planeId: flight.planeId, // Lennuki ID
          },
        });
        router.push({
          path: "/seat",
          query: {
            flightId: flight.id,
            planeId: flight.planeId,
          },
        });
      }
    return { goToSeatSelection, planeId }; // Edastame planeId template'ile
  },
  data() {
    return {
      flights: [],
      filters: {
        arrivalCity: "",
        departureCity: "",
        priceMin: 50,  // Algväärtus madalaim hind
        priceMax: 500, // Algväärtus kõrgeim hind
        dateFrom: "",
        dateTo: "",
      },
      minPrice: 50,  // Kõige odavam hind
      maxPrice: 500, // Kõige kallim hind
    };
  },
  mounted() {
    this.fetchFlights(); // Laadime lennud, kui komponent on üles ehitatud
  },
  computed: {
    filteredFlights() {
      return this.flights.filter((flight) => {
        // Filtreerimine vastavalt filtri tingimustele
        if (
            this.filters.arrivalCity &&
            !flight.arrivalCity.toLowerCase().includes(this.filters.arrivalCity.toLowerCase())
        ) {
          return false;
        }
        if (
            this.filters.departureCity &&
            !flight.departureCity.toLowerCase().includes(this.filters.departureCity.toLowerCase())
        ) {
          return false;
        }
        if (this.filters.priceMin && flight.price < this.filters.priceMin) {
          return false;
        }
        if (this.filters.priceMax && flight.price > this.filters.priceMax) {
          return false;
        }
        if (this.filters.dateFrom && new Date(flight.departOn) < new Date(this.filters.dateFrom)) {
          return false;
        }
        if (this.filters.dateTo && new Date(flight.departOn) > new Date(this.filters.dateTo)) {
          return false;
        }
        return true;
      });
    },
  },
  methods: {
    async fetchFlights() {
      try {
        const response = await axios.get("/api/flight", {
          params: { planeId: this.planeId }, // Kasutame eelmiselt lehelt saadud planeId
        });

        console.log("Serveri vastus:", response.data);
        this.flights = response.data; // Salvestame lennud
        this.updatePriceRange(); // Uuendame hinna vahemiku pärast andmete laadimist
      } catch (error) {
        console.error("Lendude laadimine ebaõnnestus:", error);
      }
    },
    formatDate(date) {
      const d = new Date(date);
      return d.toLocaleString(); // Kuupäeva formaadi kuvamine
    },
    calculateDuration(departOn, arriveOn) {
      const departTime = new Date(departOn);
      const arriveTime = new Date(arriveOn);
      const duration = (arriveTime - departTime) / 1000 / 60 / 60; // Lennu kestus tundides
      return duration.toFixed(2);
    },
    applyFilters() {
      this.filteredFlights; // See reaktiveerib lennu nimekirja
    },
    updatePriceRange() {
      // Uuendab minPrice ja maxPrice vastavalt lennu hindadele
      const prices = this.flights.map(flight => flight.price);
      this.minPrice = Math.min(...prices);
      this.maxPrice = Math.max(...prices);
      this.filters.priceMin = this.minPrice;
    },
  },
};
</script>
<style scoped>
.flight-item {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.filters {
  margin-bottom: 20px;
}

.filters label {
  margin-right: 10px;
}

.filters input {
  margin-right: 20px;
  padding: 5px;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
