package com.spring.loader;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

@RunWith(MockitoJUnitRunner.class)
public class S3PropertiesLocationRegistrarTest {

	private S3PropertiesLocationRegistrar subject;

	private Map<String, Object> attributes;

	@Mock
	private Environment environment;
	@Mock
	private AnnotationMetadata importingClassMetadata;
	@Mock
	private BeanDefinitionRegistry registry;

	@Before
	public void setup() {
		subject = new S3PropertiesLocationRegistrar();
		subject.setEnvironment(environment);

		attributes = new HashMap<>();
		attributes.put("path", new String[] { "my-bucket/my.properties" });
		attributes.put("value", new String[] { "my-bucket/my.properties" });

		when(importingClassMetadata.getAnnotationAttributes(S3PropertiesLocation.class.getName())).thenReturn(attributes);
	}

	@Test
	public void shouldLoadPropertiesWhenThereisNoProfiles() {
		attributes.put("profiles", new String[] {});

		subject.registerBeanDefinitions(importingClassMetadata, registry);

		verify(registry).registerBeanDefinition(Mockito.anyString(), Mockito.any(BeanDefinition.class));
	}

	@Test
	public void shouldLoadPropertiesWhenThereisProfilesActive() {
		String[] profiles = new String[] { "prod" };

		attributes.put("profiles", profiles);

		when(environment.acceptsProfiles(profiles)).thenReturn(true);

		subject.registerBeanDefinitions(importingClassMetadata, registry);

		verify(registry).registerBeanDefinition(Mockito.anyString(), Mockito.any(BeanDefinition.class));
	}

	@Test
	public void shouldNotLoadPropertiesWhenThereisNoProfilesActive() {
		String[] profiles = new String[] { "prod" };

		attributes.put("profiles", profiles);

		when(environment.acceptsProfiles(profiles)).thenReturn(false);

		subject.registerBeanDefinitions(importingClassMetadata, registry);

		verify(registry, never()).registerBeanDefinition(Mockito.anyString(), Mockito.any(BeanDefinition.class));
	}

}
