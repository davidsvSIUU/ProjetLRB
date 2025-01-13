package licence.projetlrb.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    // Constructeur privé
    private ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Méthode pour vérifier si la réponse est un succès
    public boolean isSuccess() {
        return success;
    }

    // Méthode pour créer une réponse de succès
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "Opération réussie", data);
    }

    // Méthode pour créer une réponse de succès avec un message personnalisé
    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data);
    }

    // Méthode pour créer une réponse d'erreur
    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }

    // Méthode pour créer une réponse d'erreur avec des données
    public static <T> ResponseDTO<T> error(String message, T data) {
        return new ResponseDTO<>(false, message, data);
    }

    // Getters et Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}