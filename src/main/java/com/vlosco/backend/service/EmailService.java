package com.vlosco.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * Service responsable de l'envoi des emails dans l'application.
 * Gère les différents types d'emails transactionnels comme la vérification 
 * de compte et la réinitialisation de mot de passe.
 */
@Service
public class EmailService {

    private JavaMailSender mailSender;

    /**
     * Constructeur injectant le JavaMailSender nécessaire pour l'envoi d'emails
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Adresse email utilisée comme expéditeur, configurée dans application.properties
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envoie un email de vérification à un nouvel utilisateur
     * @param toEmail L'adresse email du destinataire
     * @param verificationToken Le token unique de vérification
     */
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            // Configuration des informations de base de l'email
            String subject = "Verifiez votre compte";
            String verificationLink = "http://localhost:8080/users/verifyEmail?token=" + verificationToken;

            // Construction du contenu HTML de l'email avec un template structuré
            // Inclut le logo, un message de bienvenue, les fonctionnalités disponibles
            // et les instructions de vérification
            String emailContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="text-align: center; margin-bottom: 30px;">
                                <h1 style="color: #2C3F55;">Vlosco</h1>
                                <h2 style="color: #2C3F55;">Bienvenue sur Vlosco, votre plateforme de confiance pour la vente et location de véhicules !</h2>
                            </div>
                            
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                                <p>Nous sommes ravis de vous accueillir sur Vlosco ! Pour commencer votre expérience en toute sécurité, nous avons besoin de vérifier votre adresse email.</p>
                                
                                <p>Voici ce que vous pourrez faire une fois votre compte vérifié :</p>
                                <ul>
                                    <li>Publier des annonces de vente ou de location</li>
                                    <li>Contacter directement les vendeurs et loueurs</li>
                                    <li>Sauvegarder vos recherches préférées</li>
                                    <li>Recevoir des notifications personnalisées</li>
                                </ul>
                            </div>

                            <div style="text-align: center; margin: 30px 0;">
                                <p style="font-weight: bold; font-size: 18px;">Pour vérifier votre compte, cliquez sur le bouton ci-dessous :</p>
                                <a href="%s" target="_blank" style="display: inline-block; background-color: #2C3F55; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold;">Vérifier mon compte</a>
                            </div>

                            <div style="border-top: 1px solid #ddd; padding-top: 20px; margin-top: 20px;">
                                <p><small>Si le bouton ne fonctionne pas, vous pouvez copier et coller ce lien dans votre navigateur :</small></p>
                                <p><small>%s</small></p>
                                <p><small>Ce lien expirera dans 7 jours pour des raisons de sécurité.</small></p>
                            </div>

                            <div style="text-align: center; margin-top: 30px; color: #666;">
                                <p>Besoin d'aide ? Contactez notre équipe support à support@vlosco.com</p>
                                <p>&copy; 2024 Vlosco. Tous droits réservés.</p>
                            </div>
                        </body>
                    </html>
                    """.formatted(verificationLink, verificationLink);

            // Création et configuration du message avec MimeMessage pour supporter le HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true);

            // Envoi de l'email
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    /**
     * Envoie un email pour la réinitialisation du mot de passe
     * @param toEmail L'adresse email du destinataire
     * @param verificationToken Le token unique de vérification
     */
    public void sendUpdatePassword(String toEmail, String verificationToken) {
        try {
            // Configuration des informations de base de l'email
            String subject = "Modification de votre mot de passe";
            String verificationLink = "http://localhost:8080/users/verif-update-password?token=" + verificationToken;

            // Construction du contenu HTML de l'email avec un template structuré
            // Inclut des conseils de sécurité pour le choix du nouveau mot de passe
            String emailContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="text-align: center; margin-bottom: 30px;">
                                <h1 style="color: #2C3F55;">Vlosco</h1>
                                <h2 style="color: #2C3F55;">Modification de votre mot de passe</h2>
                            </div>
                            
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                                <p>Vous avez demandé la modification de votre mot de passe sur Vlosco. Pour des raisons de sécurité, nous devons vérifier votre identité.</p>
                                
                                <p>Quelques conseils pour choisir un mot de passe sécurisé :</p>
                                <ul>
                                    <li>Utilisez au moins 8 caractères</li>
                                    <li>Incluez des lettres majuscules et minuscules</li>
                                    <li>Ajoutez des chiffres et des caractères spéciaux</li>
                                    <li>Évitez les informations personnelles facilement devinables</li>
                                </ul>
                            </div>

                            <div style="text-align: center; margin: 30px 0;">
                                <p style="font-weight: bold; font-size: 18px;">Pour modifier votre mot de passe, cliquez sur le bouton ci-dessous :</p>
                                <a href="%s" target="_blank" style="display: inline-block; background-color: #2C3F55; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold;">Modifier mon mot de passe</a>
                            </div>

                            <div style="border-top: 1px solid #ddd; padding-top: 20px; margin-top: 20px;">
                                <p><small>Si le bouton ne fonctionne pas, vous pouvez copier et coller ce lien dans votre navigateur :</small></p>
                                <p><small>%s</small></p>
                                <p><small>Ce lien expirera dans 7 jours pour des raisons de sécurité.</small></p>
                            </div>

                            <div style="text-align: center; margin-top: 30px; color: #666;">
                                <p>Besoin d'aide ? Contactez notre équipe support à support@vlosco.com</p>
                                <p>&copy; 2024 Vlosco. Tous droits réservés.</p>
                            </div>
                        </body>
                    </html>
                    """.formatted(verificationLink, verificationLink);

            // Création et configuration du message avec MimeMessage pour supporter le HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true);

            // Envoi de l'email
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}
