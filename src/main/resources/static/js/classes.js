// Charger les classes au chargement de la page
document.addEventListener('DOMContentLoaded', () => {
    chargerClasses();
});

// Fonction pour charger toutes les classes
function chargerClasses() {
    fetch('/api/classe/rechercherClasses')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                afficherClasses(data.data);
            } else {
                alert('Erreur lors du chargement des classes: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            alert('Erreur lors du chargement des classes');
        });
}

// Fonction pour afficher les classes dans le tableau
function afficherClasses(classes) {
    const tbody = document.querySelector('#classeTable tbody');
    tbody.innerHTML = '';

    classes.forEach(classe => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${classe.id}</td>
            <td>${classe.nom}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="modifierClasse(${classe.id})">
                    <i class="mdi mdi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="supprimerClasse(${classe.id})">
                    <i class="mdi mdi-delete"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Fonction pour sauvegarder une classe (création ou modification)
function sauvegarderClasse() {
    const classeId = document.getElementById('classeId').value;
    const nom = document.getElementById('nom').value;

    const classe = {
        id: classeId || null,
        nom: nom
    };

    fetch('/api/classe/enregistrer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(classe)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Classe sauvegardée avec succès');
                bootstrap.Modal.getInstance(document.getElementById('classeModal')).hide();
                chargerClasses();
            } else {
                alert('Erreur: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            alert('Erreur lors de la sauvegarde');
        });
}

// Fonction pour modifier une classe
function modifierClasse(id) {
    fetch(`/api/classe/rechercher/${id}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const classe = data.data;
                document.getElementById('classeId').value = classe.id;
                document.getElementById('nom').value = classe.nom;

                const modal = new bootstrap.Modal(document.getElementById('classeModal'));
                modal.show();
            } else {
                alert('Erreur: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            alert('Erreur lors de la récupération de la classe');
        });
}

// Fonction pour supprimer une classe
function supprimerClasse(id) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette classe ?')) {
        fetch(`/api/classe/supprimer/${id}`, {
            method: 'DELETE'
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Classe supprimée avec succès');
                    chargerClasses();
                } else {
                    alert('Erreur: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Erreur:', error);
                alert('Erreur lors de la suppression');
            });
    }
}