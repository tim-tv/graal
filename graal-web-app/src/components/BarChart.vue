<script>
  import {Bar} from 'vue-chartjs'
  export default {
    props: ['dataset'],

    extends: Bar,
    data () {
      return {
        datacollection: {
          labels: this.dataset.labels,
          datasets: [
            {
              label: this.dataset.label,
              backgroundColor: '#f87979',
              pointBackgroundColor: 'white',
              borderWidth: 1,
              pointBorderColor: '#249EBF',
              data: this.dataset.values
            }
          ]
        },
        options: {
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              },
              gridLines: {
                display: true
              }
            }],
            xAxes: [ {
              gridLines: {
                display: false
              }
            }]
          },
          legend: {
            display: true
          },
          responsive: true,
          maintainAspectRatio: false
        }
      }
    },

    watch: {
      dataset: {
        handler: function (data) {
          this.datacollection.labels = data.labels
          this.datacollection.datasets[0].data = data.values
          this.datacollection.datasets[0].label = data.title
          this.renderChart(this.datacollection, this.options)
        }
      }
    },

    mounted () {
      this.renderChart(this.datacollection, this.options)
    }
  }
</script>
