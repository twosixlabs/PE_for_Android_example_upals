# Example uPAL modules

We offer the following example data transformations implemented as uPAL modules.
Usage information for the example uPALs is outlined below.

## Availability bitmap

* Identifier: `com.twosixlabs.exampleupal.AvailabilityBitmapMicroPAL`
* Description: Generates an availability bitmap showing busy/available times
* Accepted data type: calendar

* Bundle parameters
   - `resolution`: as `AvilabilityBitmapMicroPAL.Resolution.DAY`, `HOUR`,
     `HALF_HOUR`, `QUARTER_HOUR`, or `MINUTE`. Specifies the size of each
     step in the resulting bitmap.

* Bundle return
    - `window_step_millis`
    - `window_start_millis`
    - `window_end_millis`
    - `bitmap`
    - `bitmap_string`
    - `events_found`

## Name --> phone number

* Identifier: `com.twosixlabs.exampleupal.NameToNumberPAL`
* Description: Returns the phone number for a specified contact
* Accepted data type: contact

* Bundle parameters
    - `name`: A contact's name

* Bundle return
    - `phone`: The corresponding phone number from the contact list if
      available, `null` bundle otherwise

## Phone number --> name

* Identifier: `com.twosixlabs.exampleupal.NumberToNamePAL`
* Description: Returns the name for a specified contact
* Accepted data type: contact

* Bundle parameters
    - `country`: A contact's country code
    - `phone`: A contact's phone number

* Bundle return
    - `name`: The corresponding name from the contact list if
      available, `null` bundle otherwise

## Phone numbers only

* Identifier: `com.twosixlabs.exampleupal.NumbersOnlyPAL`
* Description: Return a list of all phone numbers
* Accepted data type: contact

* Bundle parameters
    - None

* Bundle return
    - `numbers`: A String array of all phone numbers in the contact list

## Zip code location

* Identifier: `com.twosixlabs.exampleupal.ZipcodeMicroPAL`
* Description: Return the US zip code for the current location, if available
* Accepted data type: location

* Bundle parameters
    - None

* Bundle return
    - `zipcode`: The zip code of the current location
    - `success`: Boolean indicating if the lookup succeeded

