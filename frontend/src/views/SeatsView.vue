<template>
  <div class="seating-view">
    <SidePanel @filtersUpdated="updateSeats" />
    <div class="seating">
      <div v-for="row in seats" :key="row.row" class="row">
        <div class="seat-group">
          <Seat
              v-for="seat in row.seats"
              :key="row.row + seat.position"
              :number="row.row + seat.position"
              :isBooked="seat.booked"
              :isRecommended="seat.recommended"
              classType="seat-item"
              @swapSeats="swapSeats"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Seat from "@/components/Seat.vue";
import SidePanel from "@/components/SidePanel.vue";

export default {
  components: { Seat, SidePanel },
  data() {
    return {
      seats: [],
      bookedSeats: [],
      recommendedSeats: [],
      availableSeats: [],
    };
  },
  async created() {
    await this.loadSeats();
  },
  methods: {
    async loadSeats() {
      try {
        const response = await axios.get("/api/seats/getSeatsByFlight", {
          params: {
            flightId: this.$route.query.flightId,
            planeId: this.$route.query.planeId,
          },
        });
        this.availableSeats = response.data.availableSeats;
        this.bookedSeats = response.data.bookedSeats;
        this.recommendedSeats = response.data.recommendedSeats;
        this.generateSeats();
      } catch (error) {
        console.error("❌ Error loading seats:", error);
      }
    },
    updateSeats(filteredSeats) {
      this.availableSeats = filteredSeats.filter(seat => !seat.booked && !seat.recommended);
      this.recommendedSeats = filteredSeats.filter(seat => seat.recommended);
      this.generateSeats();
    },
    generateSeats() {
      const seatMap = new Map(this.bookedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true]));
      const recommendedSeatMap = new Map(this.recommendedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true]));
      this.seats = Array.from({ length: 11 }, (_, index) => {
        const row = index + 1;
        let seatPositions = row === 1 ? ["C", "D", "E", "F"] :
            (row === 5 || row === 11) ? ["D", "E"] :
                ["A", "B", "C", "D", "E", "F", "G", "H"];
        return {
          row,
          seats: seatPositions.map(pos => ({
            position: pos,
            booked: seatMap.has(`${row}${pos}`),
            recommended: recommendedSeatMap.has(`${row}${pos}`),
          })),
        };
      });
    },
    swapSeats(fromSeat, toSeat) {
      let fromSeatObj = null;
      let toSeatObj = null;
      for (const row of this.seats) {
        for (const seat of row.seats) {
          const seatKey = `${row.row}${seat.position}`;
          if (seatKey === fromSeat) fromSeatObj = seat;
          if (seatKey === toSeat) toSeatObj = seat;
        }
      }

      if (fromSeatObj.recommended && !toSeatObj.recommended && !toSeatObj.booked) {
        fromSeatObj.recommended = false;
        toSeatObj.recommended = true;
      }
    }
  },
};
</script>
