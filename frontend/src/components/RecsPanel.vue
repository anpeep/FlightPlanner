<template>
  <div class="side-panel">
    <h3>Filters</h3>

    <!-- Filters Section -->
    <div class="filter">
      <input type="checkbox" id="window" v-model="filters.window" @change="applyFilters" />
      <label for="window">Window Seat</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="exit" v-model="filters.exit" @change="applyFilters" />
      <label for="exit">Near Exit</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="legroom" v-model="filters.legroom" @change="applyFilters" />
      <label for="legroom">More Legroom</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="near" v-model="filters.near" @change="applyFilters" />
      <label for="near">Near Seats</label>
    </div>

    <v-row class="align-center" justify="center">
      <v-col cols="3" md="3">
        <v-btn icon @click="decreaseSeat">
          -
        </v-btn>
      </v-col>

      <v-col cols="3" md="5">
        <v-text-field
            v-model="seatCount"
            label="Filter for"
            type="number"
            min="1"
            readonly
            outlined
            class="ticket-field"
        >
        </v-text-field>
      </v-col>

      <v-col cols="3" md="3">
        <v-btn icon @click="increaseSeat">
          +
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>
<script>
import axios from "axios";
import { decreaseSeatCount, increaseSeatCount, updateLocalStorage } from "@/utils.js";

export default {
data() {
return {
filters: {
window: false,
exit: false,
legroom: false,
near: false,
},
seatCount: localStorage.getItem('seatCount') ? parseInt(localStorage.getItem('seatCount')) : 1, // Load from localStorage
seats: [],
};
},

watch: {
seatCount(newSeatCount) {
this.applyFilters();
}
},

mounted() {
this.loadFiltersFromLocalStorage();
},

methods: {
// Save filters to localStorage when they change
applyFilters() {
try {
// Save filter states to localStorage
localStorage.setItem('filters', JSON.stringify(this.filters));

const filters = [];
if (this.filters.window) filters.push(1);
if (this.filters.exit) filters.push(2);
if (this.filters.legroom) filters.push(3);
if (this.filters.near) filters.push(4);
if (filters.length === 0) {
return;
}

const flightId = this.$route.query.flightId;
const planeId = this.$route.query.planeId;

const response = axios.post("/api/seats/addFilters", filters, {
params: {
seatCount: this.seatCount,
flightId: flightId,
planeId: planeId,
},
});
this.$emit("filtersUpdated", response.data);
} catch (error) {
console.error("❌ Error applying filters:", error.response?.data || error.message);
}
},

// Load filters from localStorage when the page is loaded
loadFiltersFromLocalStorage() {
const savedFilters = localStorage.getItem('filters');
if (savedFilters) {
this.filters = JSON.parse(savedFilters);
}
},

// Decrease seat count
decreaseSeat() {
this.seatCount = decreaseSeatCount(this.seatCount, updateLocalStorage, this.applyFilters);
},

// Increase seat count
increaseSeat() {
this.seatCount = increaseSeatCount(this.seatCount, updateLocalStorage, this.applyFilters);
},

onSeatCountUpdated(newCount) {
this.seatCount = newCount;
this.applyFilters();
},
},
};
</script>
<style scoped>
.side-panel {
  background-color: #f9f9f9;
  padding: 5vh;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 300px;
  max-height: 500px;
}

.side-panel h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.filter {
  display: flex;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.filter input[type="checkbox"] {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.filter input[type="checkbox"]:checked {
  background-color: #4caf50;
  border-color: #4caf50;
}

.filter label {
  font-size: 1rem;
  color: #555;
  cursor: pointer;
  transition: color 0.3s ease;
}

.filter label:hover {
  color: #4caf50;
}

.filter input[type="checkbox"]:hover {
  transform: scale(1.1);
}

.filter:last-child {
  margin-bottom: 0;
}

/* Apply a subtle hover effect on the side panel */
.side-panel:hover {
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.2);
  transform: translateY(-5px);
}
.ticket-field {
  width: 100%; /* Ensure the text field takes up the full width of the column */
}

.v-btn.icon {
  min-width: 30px; /* Ensure buttons have a minimum size */
}

.v-col {
  max-width: 400px; /* Ensures the ticket field doesn't become too large */
}
</style>
