<template>
  <div class="sky-background">
    <div class="clouds">
    </div>

  <v-container class="search-container">
    <v-card class="pa-5" elevation="10">
      <v-card-title class="text-h5 text-center">Search Flights</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
                v-model="departureCity"
                label="From"
                placeholder="Departure City"
                outlined
            ></v-text-field>
          </v-col>

          <!-- Arrival City -->
          <v-col cols="12" md="6">
            <v-text-field
                v-model="arrivalCity"
                label="To"
                placeholder="Arrival City"
                outlined
            ></v-text-field>
          </v-col>
        </v-row>

        <v-row>
          <!-- Ticket Count -->
          <v-col cols="12" md="6">
            <v-text-field
                v-model="ticketCount"
                label="Tickets"
                type="number"
                min="1"
                readonly
                outlined
            >
              <template v-slot:append>
                <!-- Suurenda nupp -->
                <v-btn icon @click="increaseTicketCount" aria-label="Increase Ticket Count">
                  + <!-- Plussmärk -->
                </v-btn>
              </template>

              <template v-slot:prepend>
                <!-- Vähenda nupp -->
                <v-btn icon @click="decreaseTicketCount" aria-label="Decrease Ticket Count">
                  -
                </v-btn>
              </template>


            </v-text-field>
          </v-col>

          <!-- Date Picker -->
          <v-col cols="12" md="6">
            <v-text-field
                v-model="selectedDate"
                label="Select Date"
                type="date"
                outlined
            ></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <v-divider class="my-5"></v-divider>

    <v-card v-if="flights.length" class="pa-5" elevation="5">
      <v-card-title class="text-h6">Available Flights</v-card-title>
      <v-list>
        <v-list-item
            v-for="flight in flights"
            :key="flight.id"
            @click="goToSeatSelection(flight)"
            class="flight-card"
            style="cursor: pointer; padding: 16px; border: 1px solid #e0e0e0; margin-bottom: 12px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); background-color: #fff;"
        >
          <v-list-item-content>

            <v-list-item-subtitle class="text-body-2" style="color: #152134;">
              <v-row>
                <!-- Vasakpoolne osa - Departure City ja Departure Time -->
                <v-col cols="4" class="text-left">
                  <strong>{{ flight.departureCity }}</strong>
                  <div>{{ formatInstant(flight.departOn) }}</div>
                </v-col>
                <v-col cols="4" class="text-center">
                  <div>{{ getFlightDuration(flight.departOn, flight.arriveOn) }}</div>
                </v-col>
                <v-col cols="4" class="text-right">
                  <strong>{{ flight.arrivalCity }}</strong>
                  <div>{{ formatInstant(flight.arriveOn) }}</div>
                </v-col>
              </v-row>
            </v-list-item-subtitle>

          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>
  </v-container>
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
      try {
        // Kontrollige, et kõik parameetrid on täidetud ja et need ei ole tühikud
        if (!this.selectedDate || !this.departureCity.trim() || !this.arrivalCity.trim()) {
          console.log("❌ Missing parameters: Cannot search flights");
          return;
        }

        const response = await axios.post("/api/flight/generate", null, {
          params: {
            date: this.selectedDate,
            departureCity: this.departureCity,
            arrivalCity: this.arrivalCity,
          }
        });

        console.log(response.data);

        this.flights = response.data.map(flight => ({
          planeId: flight.planeId,
          ...flight,
          flightId: flight.id,
        }));

        if (this.flights.length > 0) {
          this.flightId = this.flights[0].flightId;
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
      if (this.ticketCount < 72) {
        this.ticketCount++;
        this.updateLocalStorage();
      } else {
        console.log("❌ Ticket count cannot exceed 72");
      }
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
    getFlightDuration(departureTime, arrivalTime) {
      const departDate = new Date(departureTime);
      const arriveDate = new Date(arrivalTime);

      const durationInMinutes = (arriveDate - departDate) / 60000; // Arvutab kestuse minutites
      const hours = Math.floor(durationInMinutes / 60); // Tunnid
      const minutes = durationInMinutes % 60; // Minutid

      return `${hours}h ${minutes}m`;
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
<style scoped>
.v-btn {
  background-color: #D1EAF0;
  color: #152134;
  border-radius: 50%;
}.sky-background {
   position: fixed;
   top: 0;
   left: 0;
   width: 100%;
   height: 100%;
   background: linear-gradient(90deg, #87CEEB, #B0E0E6); /* Taevasinine värv */
   animation: skyAnimation 60s linear infinite;
   z-index: -1; /* Tagumine kiht */
 }

/* Sisu peale */
.search-container {
  position: relative;
  z-index: 1;
  overflow: hidden; /* Tagame, et sisu jääks ekraanile */
  width: 900%;
}

</style>
