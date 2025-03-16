<template>
  <v-container>

    <v-row>
      <v-col cols="4" md="4" class="mb-4">
        <v-text-field
            v-model="filters.startDate"
            label="Search From Date"
            type="date"
            @input="applyFilters"
            outlined
            dense
            color="primary"
        ></v-text-field>
      </v-col>
      <v-col cols="4">
        <h3 class="text-center mt-5">Choose Details</h3>
      </v-col>
      <v-col cols="4" md="4" class="mb-4">
        <v-text-field
            v-model="filters.endDate"
            label="To Date"
            type="date"
            @input="applyFilters"
            outlined
            dense
            color="primary"
        ></v-text-field>
      </v-col>
      </v-row>

    <v-row>
      <v-col cols="4" md="4" class="mb-4">
        <v-text-field
            v-model="filters.departureCity"
            label="Departure City"
            outlined
            dense
        />
      </v-col>

      <v-col cols="12" md="4" class="mb-4">
        <v-col cols="12" class="text-left">
          <span>{{ Math.round(filters.priceRange[0]) }} €</span>
        </v-col>
        <v-range-slider
            v-model="filters.priceRange"
            :min="minPrice"
            :max="maxPrice"
            :step="10"
            thumb-label
            :thumb-size="24"
            class="price-slider"
        />
        <v-col cols="12" class="text-right">
          <span>{{ Math.round(filters.priceRange[1]) }} €</span>
        </v-col>

      </v-col>
      <v-col cols="4" md="4" class="mb-4">
        <v-text-field
            v-model="filters.arrivalCity"
            label="Arrival City"
            outlined
            dense
        />
      </v-col>
    </v-row>

      <v-col cols="12">
        <h3 class="text-center mt-5">Choose a Flight</h3>
      </v-col>
    <v-row>
      <v-col v-if="filteredFlights.length === 0" cols="12">
        <v-alert type="info" color="light-blue" outlined>
          No flights to those filters right now, come back later!
        </v-alert>
      </v-col>

      <v-col v-if="filteredFlights.length > 0" v-for="flight in filteredFlights" :key="flight.id" cols="12" md="4">
        <v-card class="ma-2">
          <v-card-title>
            <span class="headline">{{ flight.departureCity + ' -> ' + flight.arrivalCity }}</span>
          </v-card-title>
          <v-card-subtitle>{{ formatDepartDate(flight.departOn) + ' -> ' + formatArriveDate(flight.arriveOn)}}</v-card-subtitle>

          <v-card-text>
            <p><strong>Flight duration:</strong> {{ calculateDuration(flight.departOn, flight.arriveOn) }} hours</p>
            <p><strong>Price:</strong> €{{ Math.round(flight.price) }}</p>
          </v-card-text>
          <v-card-actions>
            <v-btn @click="goToSeatSelection(flight)" color="primary">To Seats</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
import {useRoute, useRouter} from "vue-router";

export default {
  setup() {
    const router = useRouter();
    const route = useRoute();
    const planeId = route.query.planeId;

    const goToSeatSelection = (flight) => {
      axios.post("/api/seats/getSeats", null, {
        params: {
          flightId: flight.id,
          planeId: flight.planeId,
        },
      });
      router.push({
        path: "/seat",
        query: {
          flightId: flight.id,
          planeId: flight.planeId,
        },
      });
    };

    return {goToSeatSelection, planeId};
  },

  data() {
    return {
      menuStart: false, // Muutujad menüü haldamiseks
      menuEnd: false,   // Muutujad menüü haldamiseks
      flights: [],
      filters: {
        arrivalCity: "",
        departureCity: "",
        startDate: "",
        endDate: "",
        priceRange: [0, 500], // Algväärtus (min hind, max hind)
      },
      minPrice: 0,    // Miinimum hind
      maxPrice: 1000, // Maksimum hind
    };
  },

  mounted() {
    this.fetchFlights();
    this.updatePriceRange();
  },

  computed: {
    filteredFlights() {
      return this.flights.filter((flight) => {
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
        const [minPrice, maxPrice] = this.filters.priceRange;
        if (flight.price < minPrice || flight.price > maxPrice) {
          return false;
        }
        const flightDate = new Date(flight.departOn).setHours(0, 0, 0, 0);
        const startDate = this.filters.startDate ? new Date(this.filters.startDate).setHours(0, 0, 0, 0) : null;
        const endDate = this.filters.endDate ? new Date(this.filters.endDate).setHours(0, 0, 0, 0) : null;

        if (startDate && flightDate < startDate) return false;
        return !(endDate && flightDate > endDate);

      });
    },
  },
  methods: {
      applyFilters() {
        // Apply filter logic for flights
        this.filteredFlights;
      },
      async fetchFlights() {
        try {
          const response = await axios.get("/api/flight", {
            params: {planeId: this.planeId},
          });

          this.flights = response.data.map(flight => ({
            ...flight,
            departOn: new Date(flight.departOn), // Convert to Date object
            arriveOn: new Date(flight.arriveOn), // Convert to Date object
          }));

          this.updatePriceRange();
        } catch (error) {
          console.error("Error fetching flights:", error);
        }
      },
      calculateDuration(departOn, arriveOn) {
        const departTime = new Date(departOn);
        const arriveTime = new Date(arriveOn);
        const duration = (arriveTime - departTime) / 1000 / 60 / 60;
        return Math.round(duration.toFixed(2));
      },
    formatDepartDate(date) {
      const d = new Date(date);
      const options = {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false // 24-tunnine kellaaeg
      };
      return d.toLocaleString('en-GB', options).replace(',', ''); // Saadab kuupäeva nagu 01/04/2025, 09:34:05
    },
    formatArriveDate(date) {
      const d = new Date(date);
      const options = {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false // 24-tunnine kellaaeg
      };
      return d.toLocaleString('en-GB', options).replace(',', ''); // Saadab kuupäeva nagu 01/04/2025, 09:34:05
    },
    updatePriceRange() {
      // Eeldame, et lennu hinnad on olemas (näiteks `this.flights` massiivis)
      const prices = this.flights.map(flight => flight.price);
      this.minPrice = Math.min(...prices);
      this.maxPrice = Math.max(...prices);
      this.filters.priceRange = [this.minPrice, this.maxPrice]; // Seadistab algväärtuse
    },
  },

  watch: {
    // Kui hinnavahemik muutub, rakendame filtreerimist uuesti
    'filters.priceRange': function (newValue) {
      this.applyFilters(); // Igakord kui hind muutub, rakenda filtrit
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

/* Colors */
.primary {
  background-color: #6FA1FF !important;
}

.v-btn {
  background-color: #6FA1FF;
  color: white;
}

.v-btn:hover {
  background-color: #87BFFF;
}

.v-card {
  background-color: #BEDCFF;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.v-card-title {
  color: #6FA1FF;
}

.highlighted-day {
  background-color: #4CAF50 !important;
  color: white !important;
  border-radius: 50%;
}

.highlighted-range-start {
  background-color: #FFEB3B !important; /* Yellow for the start date */
  color: #000 !important;
  border-radius: 50%;
}

.highlighted-range-end {
  background-color: #FF5722 !important; /* Red for the end date */
  color: #fff !important;
  border-radius: 50%;
}

.highlighted-day-range {
  background: linear-gradient(to right, #A3CDFF, #A2F7FF) !important; /* Gradient for days between start and end */
  color: white !important;
}

/* Custom slider styling */
.v-range-slider {
  color: #A3CDFF;
}

.price-slider {
  margin-top: 20px;
}

.v-row {
  margin-top: 10px;
}

.v-col {
  font-size: 14px;
  color: #6fa1ff;
}

.v-alert {
  background-color: #BEDCFF;
  color: #6FA1FF;
}
</style>
