<script>

import {useRouter} from "vue-router";
import axios from "axios";

export default {
  data() {
    return {
      departureCity: "",
      arrivalCity: "",
      selectedDate: "",
      ticketCount:  1, // Load from localStorage
      flightId: "",
      planeId: "",
      price: null,
      dateFrom: "",
      dateTo: "",
    };
  },
  setup() {
  },
  methods: {
    async showTicket() {
        await axios.get("/api/ticket", {
          params: {
            flight: this.flightId,
            ticketCount: this.ticketCount,
          }
        })
      }}}

</script>

<template>
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

</template>

<style scoped>

</style>
