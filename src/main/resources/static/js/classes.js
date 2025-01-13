// src/main/resources/static/js/classes.js

// Fonctions de gestion des classes
function showClasseForm() {
    document.getElementById('classeForm').style.display = 'block';
}

function hideClasseForm() {
    document.getElementById('classeForm').style.display = 'none';
    document.getElementById('newClasseForm').reset();
}

async function loadClasses() {
    try {
        const response = await fetch(`${API_URL}/classe/rechercherClasses`);
        const data = await response.json();

        if (data.success) {
            const tableBody = document.getElementById('classesTableBody');
            tableBody.innerHTML = '';

            data.data.forEach(classe => {
                tableBody.innerHTML += `
                    <tr>
                        <td>${classe.id}</td>
                        <td>${classe.denomination}</td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="deleteClasse(${classe.id})">
                                Supprimer
                            </button>
                        </td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        showAlert('Erreur lors du chargement des classes', 'danger');
    }
}

async function createClasse(event) {
    event.preventDefault();

    const denomination = document.getElementById('denomination').value;

    try {
        const response = await fetch(`${API_URL}/classe/enregistrer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ denomination })
        });

        const data = await response.json();

        if (data.success) {
            showAlert('Classe créée avec succès');
            hideClasseForm();
            loadClasses();
        } else {
            showAlert(data.message, 'danger');
        }
    } catch (error) {
        showAlert('Erreur lors de la création de la classe', 'danger');
    }
}

async function deleteClasse(id) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette classe ?')) {
        try {
            const response = await fetch(`${API_URL}/classe/supprimer/${id}`, {
                method: 'DELETE'
            });

            const data = await response.json();

            if (data.success) {
                showAlert('Classe supprimée avec succès');
                loadClasses();
            } else {
                showAlert(data.message, 'danger');
            }
        } catch (error) {
            showAlert('Erreur lors de la suppression de la classe', 'danger');
        }
    }
}

// Event Listeners
document.getElementById('newClasseForm').addEventListener('submit', createClasse);