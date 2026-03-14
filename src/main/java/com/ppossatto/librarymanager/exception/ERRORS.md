# Error codes

---

## Core errors

- **ERR-11463**: Generic error, more information at the logs.


- **ERR-33946**: Generic error fetching books from the database.
    - Location: [GetAllBooksServiceImpl](./../service/impl/GetAllBooksServiceImpl.java)
    - Method: **getAllBooks**.
    - Probable causes:
        - This is the root runtime exception for JPA, so it was something that was not in the catch block. Please check
          logs


- **ERR-50729**: The query exceeded the timeout limit.
    - Location: [GetAllBooksServiceImpl](./../service/impl/GetAllBooksServiceImpl.java)
    - Method: **getAllBooks**.
    - Probable causes:
        - Database-specific limitations.
        - Misconfigured timeout settings.
        - Connection pool exhaustion.


- **ERR-56697**: The reservation code is not supported.
    - Location: [ReservationStatus](./../dto/domain/enums/ReservationStatus.java)
    - Method: **getReservationStatusByCode**.
    - Probable causes:
        - The inserted reservation code does not match any option.


- **ERR-60771**: The user status code is not supported.
    - Location: [UserStatus](./../dto/domain/enums/UserStatus.java)
    - Method: **getStatusByCode**.
    - Probable causes:
        - The inserted status code does not match any option.


- **ERR-99864**: The inserted data does is not correct by the constraints.
    - Location: Any request body or query param.
    - Probable causes:
        - The values that are not validated will be pointed and together with the reason.


- **ERR-30741**: The code to be searched is null.
    - Location: Every enum class under [enums directory](./../dto/domain/enums)
    - Method: **get(...)ByCode**.
    - Probable causes:
        - The code inserted to return the correspondent enum is null.