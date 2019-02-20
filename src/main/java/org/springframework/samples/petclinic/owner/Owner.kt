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
package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.model.Person
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Digits
import javax.validation.constraints.NotEmpty

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "owners")
class Owner : Person() {
    @Column(name = "address")
    @NotEmpty
    var address: String = ""

    @Column(name = "city")
    @NotEmpty
    var city: String = ""

    @Column(name = "telephone")
    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    var telephone: String = ""

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner")
    val pets: MutableSet<Pet> = HashSet()

    val sortedPets: List<Pet>
        get() = pets.sortedWith(compareBy { it.name })

    fun addPet(pet: Pet) {
        if (pet.isNew) pets.add(pet)
        pet.owner = this
    }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     *
     * @param name to test
     * @return true if pet name is already in use
     */
    fun getPet(name: String, ignoreNew: Boolean = false): Pet? {
        with(name.toLowerCase()) {
            return pets.filterNot { ignoreNew && it.isNew }
                    .find { it.name.toLowerCase() == this }
        }
    }
}
/**
 * Return the Pet with the given name, or null if none found for this Owner.
 *
 * @param name to test
 * @return true if pet name is already in use
 */
