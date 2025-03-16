<template>
  <v-card v-if="recommendedSeats.length" class="ticket-panel" max-width="400">
    <v-card-title class="text-h8 font-weight-bold">
      Flight Details
    </v-card-title>
    <v-skeleton-loader v-if="loading" type="card"></v-skeleton-loader>

    <v-list v-else>
      <v-list-item v-for="(item, index) in flightDetails" :key="index">
        <v-list-item-content>
          <v-list-item-title>
            <span class="font-weight-bold">{{ item.label }}:</span> {{ item.value }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-card-text>
        Flight Duration: {{ calculateFlightDuration(departOn, arriveOn) }}
      </v-card-text>
    </v-list>

    <v-divider></v-divider>

    <v-btn class="mt-4" color="primary" block @click="confirmBooking">
      Confirm Booking
    </v-btn>
  </v-card>
</template>

<script>
import { useRouter } from 'vue-router';
import axios from 'axios';
import SeatsView from "@/views/SeatsView.vue";
import { getFlightDuration } from "@/utils.js";
import { increaseSeatCount, decreaseSeatCount, updateLocalStorage } from "@/utils.js";  // Assuming these are in utils.js

export default {
  data() {
    return {
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
      loading: false, // Add loading state
      flightDetails: [], // Holds flight data in an array
    };
  },
  mounted() {
    // Load recommended seats from localStorage
    const storedRecommendedSeats = localStorage.getItem('recommendedSeats');
    if (storedRecommendedSeats) {
      this.recommendedSeats = JSON.parse(storedRecommendedSeats);
    }
    this.fetchFlightDetails();

  },
  methods: {
    calculateFlightDuration(departureTime, arrivalTime) {
      return getFlightDuration(departureTime, arrivalTime);
    },
    async fetchFlightDetails() {
      this.loading = true; // Start loading

      try {
        const response = await axios.get("/api/flight/details", {
          params: {
            flightId: this.flightId,
            planeId: this.planeId,
          }
        });


        const flightData = response.data;
        console.log(flightData.selectedDate);
        this.departureCity = flightData.departureCity;
        this.arrivalCity = flightData.arrivalCity;
        this.selectedDate = flightData.selectedDate; // This should now be a valid date string
        this.price = flightData.price;
        this.departOn = flightData.departOn;
        this.arriveOn = flightData.arriveOn;
        this.selectedDateFormatted = this.formatDate(this.selectedDate);

        this.flightDetails = [
          { label: 'From', value: this.departureCity },
          { label: 'To', value: this.arrivalCity },
          { label: 'Take off', value: this.formatTime(this.departOn) },
          { label: 'Landing', value: this.formatTime(this.arriveOn) },
        ];
      } catch (error) {
        console.error("Error fetching flight details:", error);
      } finally {
        this.loading = false; // Stop loading
      }
    },
    increaseSeat() {
      this.seatCount = increaseSeatCount(this.seatCount, updateLocalStorage, this.applyFilters);
      this.updateFlightDetails(); // Recalculate flight details
    },

    // Decrease seat count
    decreaseSeat() {
      this.seatCount = decreaseSeatCount(this.seatCount, updateLocalStorage, this.applyFilters);
      this.updateFlightDetails(); // Recalculate flight details
    },
    updateFlightDetails() {
      this.flightDetails = [
        { label: 'From', value: this.departureCity },
        { label: 'To', value: this.arrivalCity },
        { label: 'On', value: this.formatDate(this.selectedDate) },
        { label: 'Seat Count', value: this.seatCount },
        { label: 'Price', value: `€${(this.price * this.seatCount).toFixed(2)}` },
        { label: 'Take off', value: this.formatTime(this.departOn) },
        { label: 'Landing', value: this.formatTime(this.arriveOn) },
      ];
    },
    formatDate(date) {
      const d = new Date(date);
      const options = {
        year: '4-digit',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      };
      return d.toLocaleString('en-GB', options).replace(',', ''); // Saadab kuupäeva nagu 01/04/2025, 09:34:05
    },

    formatTime(date) {
      const d = new Date(date);
      return d.toLocaleTimeString([], {         month: '2-digit',
        day: '2-digit',hour: '2-digit', minute: '2-digit' });
    },

    // Updated confirmBooking method to display the price and seat count in alert
    confirmBooking() {
      const totalPrice = (this.price * parseInt(localStorage.getItem('seatCount')));
      alert(`Booking Confirmed! See you on boarding!\nSeats: ${this.seatCount}\nTotal Price: €${totalPrice}`);
    }
  }
};
</script>


<style scoped>


.ticket-panel .v-list-item-title {
  font-size: 1.1rem;
  color: #555;
}

.ticket-panel .v-btn {
  font-size: 1rem;
  font-weight: bold;
}

</style>
