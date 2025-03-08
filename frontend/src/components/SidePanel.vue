<template>
  <div class="side-panel">
    <h3>Filters</h3>

    <div class="filter">
      <input type="checkbox" id="legroom" v-model="filters.legroom" />
      <label for="legroom">More Legroom</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="exit" v-model="filters.exit" />
      <label for="exit">Near Exit</label>
    </div>

    <div class="filter">
      <input type="checkbox" id="adjacent" v-model="filters.adjacent" />
      <label for="adjacent">Adjacent Seats (for multiple tickets)</label>
    </div>

    <h3>Legend</h3>
    <ul>
      <li><span class="legend seat-business"></span> Business Class</li>
      <li><span class="legend seat-economy"></span> Economy Class</li>
      <li><span class="legend seat-booked"></span> Booked</li>
    </ul>
    <button class="continue-btn" @click="continueBooking">Continue to Book</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      filters: {
        legroom: false,
        exit: false,
        adjacent: false
      }
    };
  },
  methods: {
    continueBooking() {
      this.$router.push({ name: 'Checkout' });
    }
  }
};
</script>
