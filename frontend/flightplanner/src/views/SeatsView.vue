<template>
  <div class="seating-view">
    <SidePanel />

    <div class="seating">
      <div v-for="row in seats" :key="row.row" class="row">
        <div class="seat-group">
          <Seat
              v-for="seat in row.seats.filter(s => ['A', 'B', 'C'].includes(s.position))"
              :key="seat.position + row.row"
              :number="seat.position + row.row"
              :isBooked="seat.booked"
              :classType="seat.class"
              @toggle="toggleSeat(row.row, seat.position)"
          />
        </div>

        <div class="aisle"></div>

        <div class="seat-group">
          <Seat
              v-for="seat in row.seats.filter(s => ['D', 'F', 'G'].includes(s.position))"
              :key="seat.position + row.row"
              :number="seat.position + row.row"
              :isBooked="seat.booked"
              :classType="seat.class"
              @toggle="toggleSeat(row.row, seat.position)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Seat from "@/components/Seat.vue";
import SidePanel from "@/components/SidePanel.vue";

export default {
  components: { Seat, SidePanel },
  data() {
    return {
      seats: Array.from({ length: 17 }, (_, index) => {
        const row = index + 1;
        const isBusiness = row <= 5;
        const isTail = row > 15;

        return {
          row,
          seats: isTail
              ? [ "A", "C", "D", "F" ].map(pos => ({
                position: pos,
                class: "economy",
                booked: false
              }))
              : [ "A", "B", "C", "D", "F", "G" ].map(pos => ({
                position: pos,
                class: isBusiness ? "business" : "economy",
                booked: false
              }))
        };
      })
    };
  },
  methods: {
    toggleSeat(rowNumber, position) {
      this.seats = this.seats.map(row => {
        if (row.row === rowNumber) {
          return {
            ...row,
            seats: row.seats.map(seat =>
                seat.position === position ? { ...seat, booked: !seat.booked } : seat
            )
          };
        }
        return row;
      });
    }
  }
};
</script>

