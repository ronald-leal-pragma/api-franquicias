package com.nequi.franchise.domain.util;

import java.util.UUID;

/**
 * Utilidad para generar identificadores únicos para entidades del dominio.
 * Abstrae la generación de IDs de cualquier tecnología específica.
 */
public class IdGenerator {

    /**
     * Genera un ID único basado en UUID.
     * @return String con un ID único
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

