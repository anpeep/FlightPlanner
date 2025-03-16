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

    <v-btn block class="mt-4" color="primary" @click="confirmBooking">
      Confirm Booking
    </v-btn>
  </v-card>
</template>

<script>
import axios from 'axios';
import {getFlightDuration} from "@/utils.js";

export default {
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      selectedDate: "",
      seatCount: localStorage.getItem('seatCount') ? parseInt(localStorage.getItem('seatCount')) : 1, // Load from localStorage
      flightId: this.$route.query.flightId,
      planeId: this.$route.query.planeId,
      price: null,
      departOn: "",
      arriveOn: "",
      recommendedSeats: [],
      loading: false,
      flightDetails: [],
    };
  },
  mounted() {
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
      this.loading = true;

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
        this.selectedDate = flightData.selectedDate;
        this.price = flightData.price;
        this.departOn = flightData.departOn;
        this.arriveOn = flightData.arriveOn;

        this.flightDetails = [
          {label: 'From', value: this.departureCity},
          {label: 'To', value: this.arrivalCity},
          {label: 'Take off', value: this.formatTime(this.departOn)},
          {label: 'Landing', value: this.formatTime(this.arriveOn)},
        ];
      } catch (error) {
        console.error("Error fetching flight details:", error);
      } finally {
        this.loading = false;
      }
    },

    formatTime(date) {
      const d = new Date(date);
      return d.toLocaleTimeString([], {
        month: '2-digit',
        day: '2-digit', hour: '2-digit', minute: '2-digit'
      });
    },

    confirmBooking() {
      const totalPrice = Math.round(this.price * parseInt(localStorage.getItem('seatCount')));
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
