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

    <!-- Pileti arv koos nuppudega -->
    <div class="input-group">
      <label>Tickets:</label>
      <button @click="decreaseTicketCount">-</button>
      <input v-model="ticketCount" type="number" min="1" readonly />
      <button @click="increaseTicketCount">+</button>
    </div>

    <div class="input-group">
      <label>Select Date:</label>
      <input type="date" v-model="selectedDate" @change="searchFlights" />
    </div>

    <h3 v-if="flights.length">Available Flights</h3>
    <ul v-if="flights.length">
      <li
          v-for="flight in flights"
          :key="flight.id"
          @click="goToSeatSelection(flight.id)"
          style="cursor: pointer;"
      >
        <strong>{{ flight.departure_city }} → {{ flight.arrival_city }}</strong><br />
        🛫 Departure: {{ formatInstant(flight.departOn) }} <br />
        🛬 Arrival: {{ formatInstant(flight.arriveOn) }} <br />
        ✈️ Plane ID: {{ flight.planeId }} <!-- Display the plane ID -->
      </li>
    </ul>
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
      selectedDate: "",
      ticketCount: 1, // Vaikimisi 1 pilet
      flights: [],
      planeId: "",
      flightId: "",
    };
  },
  setup() {
    const router = useRouter();

    const goToSeatSelection = (flightId) => {
      router.push({ path: '/seat', query: { flightId } });
    };

    return { goToSeatSelection };
  },
  methods: {
    async searchFlights() {
      if (!this.departureCity || !this.arrivalCity || !this.selectedDate) {
        return;
      }

      try {
        // Step 1: Fetch flights
        const response = await axios.post("/api/flight/generate", null, {
          params: {
            departureCity: this.departureCity,
            arrivalCity: this.arrivalCity,
            date: this.selectedDate
          }
        });

        this.flights = response.data.map(flight => ({
          ...flight,
          flightId: flight.id,  // Assign flightId
          planeId: flight.planeId // Assign planeId
        }));

        if (this.flights.length > 0) {
          // Use the first flight for now (modify as needed)
          this.flightId = this.flights[0].flightId;
          this.planeId = this.flights[0].planeId;

          // Step 2: Fetch available seats using the flight & plane IDs
          await axios.post("/api/seats/getAvailableSeats", null, {
            params: {
              seatCount: this.ticketCount,
              flightId: this.flightId,
              planeId: this.planeId
            }
          });
        }
      } catch (error) {
        console.error("Error fetching flights:", error);


    // Fetch flights for a specific plane (use planeId if available, else fetch flights normally)
        const response = await axios.post("/api/flight/generate", null, {
          params: {
            departureCity: this.departureCity,
            arrivalCity: this.arrivalCity,
            date: this.selectedDate
          }
        });

        // After fetching flights, associate them with the correct plane
        this.flights = response.data.map(flight => ({
          ...flight,
          planeId: flight.planeId
        }));
      }
    },

    // Pileti arvu muutmise funktsioonid
    increaseTicketCount() {
      this.ticketCount++;
    },
    decreaseTicketCount() {
      if (this.ticketCount > 1) {
        this.ticketCount--;
      }
    },

    formatInstant(instant) {
      if (!instant) return "N/A";
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
