document.addEventListener('DOMContentLoaded', function() {
    // Graphique des moyennes par classe
    const ctx = document.getElementById('moyennesChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['6èmeA', '6èmeB', '5èmeA', '5èmeB', '4èmeA', '4èmeB', '3èmeA', '3èmeB'],
                datasets: [{
                    label: 'Moyenne générale',
                    data: [12.5, 13.2, 11.8, 14.1, 12.9, 13.5, 14.2, 13.8],
                    backgroundColor: '#4a90e2',
                    borderColor: '#4a90e2',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 20
                    }
                }
            }
        });
    }
});