<template>
  <div class="search-container">
    <h2>Search Flights</h2>

    <div class="input-group">
      <label>From:</label>
      <input v-model="departureCity" placeholder="Departure City" />
    </div>

    <div class="input-group">
      <label>To:</label>
      <input v-model="arrivalCity" placeholder="Arrival City" />
    </div>

    <div class="input-group">
      <label>Tickets:</label>
      <button @click="decreaseTicketCount">-</button>
      <input v-model="ticketCount" type="number" min="1" readonly />
      <button @click="increaseTicketCount">+</button>
    </div>

    <div class="input-group">
      <label>Select Date:</label>
      <input type="date" v-model="selectedDate" />
    </div>

    <h3 v-if="flights.length">Available Flights</h3>
    <div class="flights-list" v-if="flights.length">
      <div
          v-for="flight in flights"
          :key="flight.id"
          @click="goToSeatSelection(flight)"
          class="flight-card"
          style="cursor: pointer;"
      >
        <strong>{{ flight.departureCity }} → {{ flight.arrivalCity }}</strong>
        <div>🛫 Departure: {{ formatInstant(flight.departOn) }}</div>
        <div>🛬 Arrival: {{ formatInstant(flight.arriveOn) }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { useRouter } from 'vue-router';

export default {
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      selectedDate: null, // Siia salvestatakse valitud kuupäev
      ticketCount: localStorage.getItem('ticketCount') ? parseInt(localStorage.getItem('ticketCount')) : 1, // Load from localStorage
      flights: [],
      flightId: "",
      planeId: "",
    };
  },
  setup() {
    const router = useRouter();

    const goToSeatSelection = (flight) => {
      console.log("Navigating with flightId:", flight.id, "and planeId:", flight.planeId);

      router.push({
        path: '/seat',
        query: {
          flightId: flight.id,
          planeId: flight.planeId
        }
      });
    };

    return { goToSeatSelection };
  },
  watch: {
    // Watch for changes in departureCity, arrivalCity, or selectedDate
    departureCity() {
      this.searchFlights();
    },
    arrivalCity() {
      this.searchFlights();
    },
    selectedDate() {
      this.searchFlights();
    }
  },
  methods: {
    async searchFlights() {
      // Only run if all fields are filled
      if (!this.departureCity || !this.arrivalCity || !this.selectedDate) {
        return; // Exit early if any of the fields are empty
      }

      try {
        const response = await axios.post("/api/flight/generate", null, {
          params: {
            departureCity: this.departureCity,
            arrivalCity: this.arrivalCity,
            date: this.selectedDate, // Use the date as entered, formatted to 'YYYY-MM-DD'
          }
        });
        console.log(response.data);

        this.flights = response.data.map(flight => ({
          planeId: flight.planeId,
          ...flight,
          flightId: flight.id,
        }));

        if (this.flights.length > 0) {
          // Use the first flight for now (modify as needed)
          this.flightId = this.flights[0].flightId;

          // Step 2: Fetch available seats using the flight & plane IDs
          await axios.post("/api/seats/getSeats", null, {
            params: {
              flightId: this.flightId,
              seatCount: this.ticketCount,
              planeId: this.flights[0].planeId
            }
          });
        }
      } catch (error) {
        console.error("Error fetching flights:", error);
      }
    },

    increaseTicketCount() {
      this.ticketCount++;
      this.updateLocalStorage();
    },
    decreaseTicketCount() {
      if (this.ticketCount > 1) {
        this.ticketCount--;
        this.updateLocalStorage();
      }
    },

    updateLocalStorage() {
      localStorage.setItem('ticketCount', this.ticketCount);
    },

    formatInstant(instant) {
      return new Date(instant).toLocaleString("et-EE", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
      });
    }
  }
};
</script>
