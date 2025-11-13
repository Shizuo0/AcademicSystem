package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import model.SituacaoAcademica;
import model.StatusDisponibilidade;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonParser {

    private final Gson gson;

    public GsonParser() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(StatusDisponibilidade.class, new StatusDisponibilidadeDeserializer())
                .registerTypeAdapter(SituacaoAcademica.class, new SituacaoAcademicaDeserializer())
                .create();
    }

    public <T> T parseObject(String json, Class<T> classOfT) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            System.err.println("[ERRO] Falha ao fazer parse de " + classOfT.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    public <T> List<T> parseList(String json, Class<T> classOfT) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = TypeToken.getParameterized(List.class, classOfT).getType();
            List<T> result = gson.fromJson(json, listType);
            return result != null ? result : new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.err.println("[ERRO] Falha ao fazer parse de lista de " + classOfT.getSimpleName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static class StatusDisponibilidadeDeserializer implements JsonDeserializer<StatusDisponibilidade> {
        @Override
        public StatusDisponibilidade deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonPrimitive()) {
                if (json.getAsJsonPrimitive().isBoolean()) {
                    return json.getAsBoolean() ? StatusDisponibilidade.DISPONIVEL : StatusDisponibilidade.INDISPONIVEL;
                } else if (json.getAsJsonPrimitive().isString()) {
                    String value = json.getAsString().toUpperCase();
                    try {
                        return StatusDisponibilidade.valueOf(value);
                    } catch (IllegalArgumentException e) {
                        return value.contains("DISP") ? StatusDisponibilidade.DISPONIVEL : StatusDisponibilidade.INDISPONIVEL;
                    }
                }
            }
            return StatusDisponibilidade.DISPONIVEL;
        }
    }

    private static class SituacaoAcademicaDeserializer implements JsonDeserializer<SituacaoAcademica> {
        @Override
        public SituacaoAcademica deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                String value = json.getAsString().toUpperCase();
                try {
                    return SituacaoAcademica.valueOf(value);
                } catch (IllegalArgumentException e) {
                    if (value.contains("ATIVO")) {
                        return SituacaoAcademica.ATIVO;
                    } else if (value.contains("TRANC")) {
                        return SituacaoAcademica.TRANCADO;
                    }
                }
            }
            return SituacaoAcademica.ATIVO;
        }
    }
}
