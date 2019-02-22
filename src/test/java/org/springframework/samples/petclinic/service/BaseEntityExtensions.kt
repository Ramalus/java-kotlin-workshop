/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service

import org.springframework.samples.petclinic.model.BaseEntity

/**
 * Utility methods for handling entities. Separate from the BaseEntity class mainly because of dependency on the
 * ORM-associated ObjectRetrievalFailureException.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see org.springframework.samples.petclinic.model.BaseEntity
 *
 * @since 29.10.2003
 */

inline fun <reified T : BaseEntity> Collection<T>.getById(id: Int): T {
    return this.first { it.id == id && T::class.java.isInstance(it) }
}
