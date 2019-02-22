/*
 * Copyright 2012-2018 the original author or authors.
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

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.samples.petclinic.model.BaseEntity
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetRepository
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.samples.petclinic.visit.Visit
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.stereotype.Service
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

import org.assertj.core.api.Assertions.assertThat

/**
 * Integration test of the Service and the Repository layer.
 *
 *
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided by the Spring
 * TestContext Framework:    * **Spring IoC container caching** which spares us unnecessary set up
 * time between test execution.  * **Dependency Injection** of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of [@Autowired][Autowired] on the `[ ][ClinicServiceTests.clinicService]` instance variable, which uses autowiring *by
 * type*.  * **Transaction management**, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script.  *  An [ ApplicationContext][org.springframework.context.ApplicationContext] is also inherited and can be used for explicit bean lookup if necessary.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */

@RunWith(SpringRunner::class)
@DataJpaTest(includeFilters = [ComponentScan.Filter(Service::class)])
class ClinicServiceTests {

    @Autowired
    protected lateinit var owners: OwnerRepository

    @Autowired
    protected lateinit var pets: PetRepository

    @Autowired
    protected lateinit var visits: VisitRepository

    @Autowired
    protected lateinit var vets: VetRepository

    @Test
    fun shouldFindOwnersByLastName() {
        var owners = this.owners.findByLastName("Davis")
        assertThat(owners.size).isEqualTo(2)

        owners = this.owners.findByLastName("Daviss")
        assertThat(owners.isEmpty()).isTrue()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = this.owners.findById(1)
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.pets.size).isEqualTo(1)
        assertThat(owner.sortedPets[0].type).isNotNull
        assertThat(owner.sortedPets[0].type!!.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        var owners = this.owners.findByLastName("Schultz")
        val found = owners.size

        val owner = Owner()
        owner.firstName = "Sam"
        owner.lastName = "Schultz"
        owner.address = "4, Evans Street"
        owner.city = "Wollongong"
        owner.telephone = "4444444444"
        this.owners.save(owner)
        assertThat(owner.id?.toLong()).isNotEqualTo(0)

        owners = this.owners.findByLastName("Schultz")
        assertThat(owners.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        var owner = this.owners.findById(1)
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"

        owner.lastName = newLastName
        this.owners.save(owner)

        // retrieving new name from database
        owner = this.owners.findById(1)
        assertThat(owner.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindPetWithCorrectId() {
        val pet7 = this.pets.findById(7)
        assertThat(pet7.name).startsWith("Samantha")
        assertThat(pet7.owner?.firstName).isEqualTo("Jean")
    }

    @Test
    fun shouldFindAllPetTypes() {
        val petTypes = this.pets.findPetTypes()

        val petType1 = petTypes.getById(1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType4 = petTypes.getById(4)
        assertThat(petType4.name).isEqualTo("snake")
    }

    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        var owner6 = this.owners.findById(6)
        val found = owner6.pets.size

        val pet = Pet()
        pet.name = "bowser"
        val types = this.pets.findPetTypes()
        pet.type = types.getById(2)
        pet.birthDate = LocalDate.now()
        owner6.addPet(pet)
        assertThat(owner6.pets.size).isEqualTo(found + 1)

        this.pets.save(pet)
        this.owners.save(owner6)

        owner6 = this.owners.findById(6)
        assertThat(owner6.pets.size).isEqualTo(found + 1)
        // checks that id has been generated
        assertThat(pet.id).isNotNull()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun shouldUpdatePetName() {
        var pet7 = this.pets.findById(7)
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        this.pets.save(pet7)

        pet7 = this.pets.findById(7)
        assertThat(pet7.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindVets() {
        val vets = this.vets.findAll()

        val vet = vets.getById(3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.nrOfSpecialties).isEqualTo(2)
        assertThat(vet.specialties[0].name).isEqualTo("dentistry")
        assertThat(vet.specialties[1].name).isEqualTo("surgery")
    }

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        var pet7 = this.pets.findById(7)
        val found = pet7.visits.size
        val visit = Visit()
        pet7.addVisit(visit)
        visit.description = "test"
        this.visits.save(visit)
        this.pets.save(pet7)

        pet7 = this.pets.findById(7)
        assertThat(pet7.visits.size).isEqualTo(found + 1)
        assertThat(visit.id).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun shouldFindVisitsByPetId() {
        val visits = this.visits.findByPetId(7)
        assertThat(visits.size).isEqualTo(2)
        val visitArr = visits.toTypedArray()
        assertThat(visitArr[0].date).isNotNull()
        assertThat(visitArr[0].petId).isEqualTo(7)
    }
}
