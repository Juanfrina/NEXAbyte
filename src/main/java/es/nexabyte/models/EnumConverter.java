package es.nexabyte.models;

import org.apache.commons.beanutils.Converter;

/**
 * Conversor de enums para la API BeanUtils.
 * Permite convertir cadenas de texto a valores de enumeración
 * al poblar los beans desde los formularios.
 * 
 * @author jfco1
 */
public class EnumConverter implements Converter {

    /**
     * Convierte un valor a un tipo enum.
     * 
     * @param type la clase del enum destino.
     * @param value el valor a convertir (normalmente un String).
     * @return el valor del enum correspondiente.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Class type, Object value) {
        if (value == null || value.toString().isEmpty()) {
            return null;
        }
        return Enum.valueOf(type, value.toString());
    }
}
