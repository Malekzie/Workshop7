package com.sait.peelin.repository;

import com.sait.peelin.model.User;
import com.sait.peelin.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("coreUserRepository")
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUsernameOrUserEmail(String username, String userEmail);

    /** Same identifier for both args matches either username or email (login field). */
    Optional<User> findByUsernameIgnoreCaseOrUserEmailIgnoreCase(String username, String userEmail);

    @Query("""
            SELECT u FROM User u
            WHERE u.active = true
              AND (
                lower(trim(u.username)) = lower(trim(:principal))
                OR lower(trim(u.userEmail)) = lower(trim(:principal))
              )
            """)
    List<User> findAllActiveByLoginPrincipal(@Param("principal") String principal);

    boolean existsByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserEmailIgnoreCase(String userEmail);

    boolean existsByUsernameIgnoreCaseAndUserIdNot(String username, UUID userId);

    boolean existsByUserEmailIgnoreCaseAndUserIdNot(String userEmail, UUID userId);

    /** True if a customer account already uses this sign-in email (case-insensitive). */
    boolean existsByUserEmailIgnoreCaseAndUserRole(String userEmail, UserRole userRole);

    /**
     * Another customer or admin (not {@code excludeUserId}) already uses this email.
     * Employee rows may share the same address for linking; they do not block here.
     */
    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM User u
            WHERE u.userId <> :excludeUserId
              AND LOWER(TRIM(u.userEmail)) = LOWER(TRIM(:email))
              AND u.userRole IN (com.sait.peelin.model.UserRole.customer, com.sait.peelin.model.UserRole.admin)
            """)
    boolean existsOtherCustomerOrAdminWithEmailIgnoreCase(
            @Param("email") String email,
            @Param("excludeUserId") UUID excludeUserId);

    @Modifying
    @Query(value = """
        UPDATE "user"
        SET profile_photo_path = :photoPath,
            photo_approval_pending = :pending
        WHERE user_id = :userId
        """, nativeQuery = true)
    int updateProfilePhotoState(
            @Param("userId") UUID userId,
            @Param("photoPath") String photoPath,
            @Param("pending") boolean pending
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "user"
        SET username = :username,
            user_email = :email
        WHERE user_id = :userId
        """, nativeQuery = true)
    int updateAccountIdentity(
            @Param("userId") UUID userId,
            @Param("username") String username,
            @Param("email") String email
    );
}
