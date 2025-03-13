<template>
  <div class="side-panel">
    <h3>Filters</h3>

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
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      filters: {
        window: false,
        exit: false,
        legroom: false,
        near: false,
      },

      seats: [],  // Toolide andmed, mida kuvada
    };
  },
  methods: {
      async applyFilters() {
        try {
          const filters = [];

          // Koguge aktiivsed filtrid
          if (this.filters.window) filters.push(1);   // Akna tooli filter
          if (this.filters.exit) filters.push(2);     // Väljapääsu tooli filter
          if (this.filters.legroom) filters.push(3);   // Rohkem jalaruumi filter
          if (this.filters.near && localStorage.getItem('ticketCount') > 1) filters.push(4); // Lähedal olevate toolide filter, ainult kui rohkem kui 1 pilet

          const flightId = this.$route.query.flightId;  // Lennu ID URL-ist
          const planeId = this.$route.query.planeId;    // Lennuki ID URL-ist

          // Kontrollige, kas flightId ja planeId on olemas
          if (!flightId || !planeId) {
            console.error("❌ Missing flightId or planeId");
            alert("Lennu ja lennuki ID-d puuduvad! Kontrollige URL-i.");
            return;
          }

          const seatCount = localStorage.getItem('ticketCount') || 1; // Pileti arvu toomine localStorage-st

          // Saadame filtrid koos parameetritega serverisse
          const response = await axios.post("/api/seats/addFilters", filters, {
            params: {
              seatCount: seatCount,
              flightId: flightId,  // Lennu ID päringu parameetritena
              planeId: planeId     // Lennuki ID päringu parameetritena
            }
          });

          console.log("✅ Filters applied:", response.data);
          this.$emit("filtersUpdated", response.data);  // Saadame värskendatud toolide andmed vanemale
        } catch (error) {
          console.error("❌ Error applying filters:", error.response?.data || error.message);
        }

    }
  },
};
</script>
