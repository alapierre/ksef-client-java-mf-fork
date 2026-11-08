package io.alapierre.ksef.json.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.alapierre.ksef.json.annotation.JsonName;
import io.alapierre.ksef.json.annotation.JsonValue;

/**
 * Maps the library-neutral JSON annotations to Jackson behavior.
 * Register before using an ObjectMapper directly with KSeF models.
 * JacksonJsonSerializer installs this module on its own mapper automatically.
 */
public final class KsefJsonModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public KsefJsonModule() {
        super("KsefJsonModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new KsefAnnotationIntrospector());
    }

    private static final class KsefAnnotationIntrospector extends NopAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public PropertyName findNameForSerialization(Annotated member) {
            JsonName name = member.getAnnotation(JsonName.class);
            return name == null ? null : PropertyName.construct(name.value());
        }

        @Override
        public PropertyName findNameForDeserialization(Annotated member) {
            return findNameForSerialization(member);
        }

        @Override
        public Boolean hasAsValue(Annotated member) {
            return member.hasAnnotation(JsonValue.class) ? Boolean.TRUE : null;
        }

        @Override
        public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated member) {
            return member.hasAnnotation(io.alapierre.ksef.json.annotation.JsonCreator.class)
                    ? JsonCreator.Mode.DELEGATING : null;
        }
    }
}
