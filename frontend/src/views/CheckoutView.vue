<template>
  <div class="checkout-view">
    <h2>Checkout</h2>

    <div class="form-group">
      <label>From:</label>
      <input type="text" v-model="checkoutData.from" disabled />
    </div>

    <div class="form-group">
      <label>To:</label>
      <input type="text" v-model="checkoutData.to" disabled />
    </div>

    <div class="form-group">
      <label>Airplane:</label>
      <input type="text" v-model="checkoutData.airplane" disabled />
    </div>

    <div class="form-group">
      <label>Date:</label>
      <input type="text" v-model="checkoutData.date" disabled />
    </div>

    <div class="form-group">
      <label>Passenger Count:</label>
      <input type="text" v-model="checkoutData.count" disabled />
    </div>

    <div class="form-group">
      <label>Seat Filters:</label>
      <input type="text" v-model="checkoutData.filters" disabled />
    </div>

    <div class="form-group">
      <label>Price:</label>
      <input type="text" v-model="checkoutData.price" disabled />
    </div>

    <button class="pay-now" @click="payNow">Pay Now</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      checkoutData: {
        from: '',
        to: '',
        airplane: '',
        date: '',
        count: '',
        filters: '',
        price: ''
      }
    };
  },
  mounted() {
    this.fetchCheckoutData();
  },
  methods: {
    async fetchCheckoutData() {
      try {
        const responses = await Promise.all([
          axios.get('/ticket/departure/airport'),
          axios.get('/ticket/departure/airport'),
          axios.get('/ticket/airplane'),
          axios.get('/ticket/date'),
          axios.get('/ticket/count/'),
          axios.get('/ticket/filters/'),
          axios.get('/ticket/price/')
        ]);

        this.checkoutData.from = responses[0].data;
        this.checkoutData.to = responses[1].data;
        this.checkoutData.airplane = responses[2].data;
        this.checkoutData.date = responses[3].data;
        this.checkoutData.count = responses[4].data;
        this.checkoutData.filters = responses[5].data;
        this.checkoutData.price = responses[6].data;
      } catch (error) {
        console.error('Error fetching checkout data:', error);
      }
    },
    payNow() {
      alert('Proceeding to payment...');
    }
  }
};
</script>
