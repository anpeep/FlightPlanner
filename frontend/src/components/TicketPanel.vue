<template>
  <div v-if="recommendedSeats.length" class="ticket-panel">
    <h3>Flight Details</h3>
    <ul>
      <li><strong>Departure City:</strong> {{ departureCity }}</li>
      <li><strong>Arrival City:</strong> {{ arrivalCity }}</li>
      <li><strong>Flight Date:</strong> {{ formatDate(departOn) }}</li>
      <li><strong>Seat Count:</strong> {{ seatCount }}</li>
      <li><strong>Flight ID:</strong> {{ flightId }}</li>
      <li><strong>Plane ID:</strong> {{ planeId }}</li>
      <li><strong>Price:</strong> €{{ price }}</li>
      <li><strong>From Date:</strong> {{ formatTime(departOn) }}</li>
      <li><strong>To Date:</strong> {{ formatTime(arriveOn) }}</li>
    </ul>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import axios from "axios";

export default {
  data() {
    return {
      // Define data properties
      departureCity: "",
      arrivalCity: "",
      selectedDate: "",
      seatCount: localStorage.getItem('seatCount') ? parseInt(localStorage.getItem('seatCount')) : 1, // Load from localStorage
      flightId: this.$route.query.flightId,  // Read flightId from URL query
      planeId: this.$route.query.planeId,    // Read planeId from URL query
      price: null,
      departOn: "",
      arriveOn: "",
      recommendedSeats: [], // Add this to store the recommended seats
    };
  },
  mounted() {
    // Load recommended seats from localStorage
    const storedRecommendedSeats = localStorage.getItem('recommendedSeats');
    if (storedRecommendedSeats) {
      this.recommendedSeats = JSON.parse(storedRecommendedSeats);
    }

    // Fetch flight details from API or wherever you are getting these values from
    this.fetchFlightDetails();
  },
  methods: {
    async fetchFlightDetails() {
      try {
        const response = await axios.get("/api/flight/details", {
          params: {
            flightId: this.flightId,
            planeId: this.planeId,
          }
        });
        const flightData = response.data;
        this.departureCity = flightData.departureCity;
        this.arrivalCity = flightData.arrivalCity;
        this.selectedDate = flightData.selectedDate;
        this.price = flightData.price;
        this.departOn = flightData.departOn;
        this.arriveOn = flightData.arriveOn;
      } catch (error) {
        console.error("Error fetching flight details:", error);
      }
    },
    formatTime(date) {
      // Kuvab ainult kellaaega (HH:mm:ss formaadis)
      const d = new Date(date);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatDate(date) {
      // Kuvab kuupäeva kuu ja kuupäevaga (kuu-päev-aasta formaadis)
      const d = new Date(date);
      return d.toLocaleDateString([], { year: 'numeric', month: 'long', day: 'numeric' });
    }
  }
};
</script>


<style scoped>
.ticket-panel {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 350px;
  margin-left: 20px;
  flex-shrink: 0;
}

.ticket-panel h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.ticket-panel ul {
  list-style-type: none;
  padding-left: 0;
}

.ticket-panel li {
  font-size: 1rem;
  color: #555;
  margin-bottom: 10px;
}

.ticket-panel li:last-child {
  margin-bottom: 0;
}
</style>
