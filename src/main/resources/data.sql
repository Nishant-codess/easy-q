-- Seed data for Easy-Q (H2 in-memory)
-- Services shown in the booking dropdown

-- Demo customer user (used automatically by booking without auth)
INSERT INTO users (username, email, password, first_name, last_name, role, is_active) VALUES
  ('demo_customer', 'demo.customer@example.com', 'password', 'Demo', 'Customer', 'CUSTOMER', TRUE);

INSERT INTO services (name, description, duration_minutes, price, is_active) VALUES
  ('General Consultation', 'General health consultation with a physician', 30, 100.00, TRUE),
  ('Dental Checkup', 'Regular dental examination and cleaning', 45, 150.00, TRUE),
  ('Eye Examination', 'Comprehensive eye examination', 20, 80.00, TRUE),
  ('Blood Test', 'Complete blood count and basic tests', 15, 50.00, TRUE),
  ('X-Ray', 'Chest X-ray examination', 10, 75.00, TRUE),
  -- Additional services related to the project
  ('Vaccination', 'Routine immunization and travel vaccines', 20, 60.00, TRUE),
  ('Physiotherapy Session', 'Personalized physical therapy session', 40, 120.00, TRUE),
  ('Telemedicine Consultation', 'Virtual consultation with a doctor', 25, 70.00, TRUE),
  ('MRI Scan', 'Magnetic Resonance Imaging scan', 60, 500.00, TRUE),
  ('COVID-19 Test', 'Rapid antigen/PCR testing', 15, 40.00, TRUE);


