-- Seed data for CRM database

-- Admin user (password: secret123)
INSERT INTO users (name, email, password) VALUES ('Admin User', 'admin@crm.com', '$2b$10$yzbSyPNMGoT2wv/6jJR30ug7wQUx62zlijPs0JrZqFOhe270Dy1Nm');

-- Companies
INSERT INTO companies (name, industry, user_id) VALUES ('Northstar Labs', 'Technology', 1);
INSERT INTO companies (name, industry, user_id) VALUES ('Verve Studio', 'Marketing', 1);
INSERT INTO companies (name, industry, user_id) VALUES ('Lumen HQ', 'Consulting', 1);
INSERT INTO companies (name, industry, user_id) VALUES ('NovaWorks', 'Software', 1);
INSERT INTO companies (name, industry, user_id) VALUES ('Summit Labs', 'Biotech', 1);

-- Customers
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Alice Brown', 'alice@northstar.com', '+1-555-0101', 'Northstar Labs', 'CTO', 'Active', 'Key technical decision maker', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Marcus Lee', 'marcus@verve.com', '+1-555-0102', 'Verve Studio', 'CEO', 'Active', 'Interested in enterprise plan', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Priya Shah', 'priya@lumen.com', '+1-555-0103', 'Lumen HQ', 'VP Sales', 'Lead', 'Met at conference', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Carlos Diaz', 'carlos@novaworks.com', '+1-555-0104', 'NovaWorks', 'Engineering Director', 'Qualified', 'Needs integration support', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Emma Hart', 'emma@summitlabs.com', '+1-555-0105', 'Summit Labs', 'Head of Operations', 'Customer', 'Long-term client', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Lucas Reed', 'lucas@signalforge.com', '+1-555-0106', 'Signal Forge', 'Product Manager', 'Active', 'Looking for analytics tools', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Sophia Chen', 'sophia@northstar.com', '+1-555-0107', 'Northstar Labs', 'Product Owner', 'Lead', 'Referred by Alice Brown', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('James Wilson', 'james@verve.com', '+1-555-0108', 'Verve Studio', 'Marketing Director', 'Active', 'Handles digital campaigns', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Olivia Martin', 'olivia@lumen.com', '+1-555-0109', 'Lumen HQ', 'Consultant', 'Qualified', 'Needs custom reporting', CURRENT_TIMESTAMP, 1);
INSERT INTO customers (name, email, phone, company_name, position, status, notes, created_at, user_id) VALUES ('Daniel Kim', 'daniel@novaworks.com', '+1-555-0110', 'NovaWorks', 'DevOps Lead', 'Customer', 'Happy with current service', CURRENT_TIMESTAMP, 1);

-- Leads
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Alicia Brown', 'Northstar Labs', 42000.00, 'NEW', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Marcus Lee', 'Verve Studio', 18000.00, 'CONTACTED', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Priya Shah', 'Lumen HQ', 78000.00, 'QUALIFIED', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Carlos Diaz', 'NovaWorks', 26000.00, 'PROPOSAL', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Emma Hart', 'Summit Labs', 92000.00, 'WON', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Lucas Reed', 'Signal Forge', 12000.00, 'LOST', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('Sophia Chen', 'Northstar Labs', 35000.00, 'NEW', 1);
INSERT INTO leads (name, company, lead_value, status, user_id) VALUES ('James Wilson', 'Verve Studio', 55000.00, 'CONTACTED', 1);

-- Tasks
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Send proposal to Northstar Labs', 'Prepare and send enterprise proposal with custom pricing.', 'IN_PROGRESS', 'HIGH', '1', '2026-09-01T17:00:00', 'Alice Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Schedule onboarding call with NovaWorks', 'Coordinate with team to schedule initial training session.', 'TODO', 'MEDIUM', '4', '2026-08-31T17:00:00', 'Bob Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Follow up on Verve Studio contract', 'Check if they have reviewed the proposal.', 'TODO', 'HIGH', '2', '2026-08-30T14:00:00', 'Alice Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Update CRM with competitor analysis', 'Add notes about competitor tools they mentioned during call.', 'DONE', 'LOW', '1', '2026-08-28T17:00:00', 'Carol Davis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Prepare demo for new features', 'Record demo of reporting module and integration features.', 'IN_PROGRESS', 'URGENT', '1', '2026-08-29T17:00:00', 'Alice Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Review Lumen HQ contract terms', 'Legal review of the proposed SLA and terms.', 'TODO', 'MEDIUM', '3', '2026-09-05T17:00:00', 'Legal Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Conduct training for Summit Labs', 'Onboarding session for the operations team.', 'TODO', 'HIGH', '5', '2026-09-10T14:00:00', 'Training Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO tasks (title, description, status, priority, customer_id, due_date, assigned_to, created_at, updated_at, user_id) VALUES ('Finalize Q3 pipeline report', 'Compile all deal data for quarterly review.', 'IN_PROGRESS', 'MEDIUM', '2', '2026-09-15T17:00:00', 'Analytics Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- Activities
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('1', 'Discovery Call with Northstar Labs', 'Discussed their Q4 roadmap and pain points. Interested in our platform.', 'CALL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('2', 'Sent proposal for Verve Studio', 'Enterprise plan with custom integrations. Following up in 5 days.', 'EMAIL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('3', 'Contract signed - NovaWorks', 'Annual license agreement signed. Onboarding starts next week.', 'MEETING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('1', 'Follow-up on implementation timeline', 'Added note to schedule training session for their team.', 'NOTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('2', 'Demo session completed', 'Showed key features. Demo went well, client wants to see reporting module.', 'CALL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('4', 'Technical requirements review', 'Reviewed API integration requirements with NovaWorks engineering team.', 'MEETING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('5', 'Quarterly business review', 'Discussed usage metrics and upcoming needs for Summit Labs.', 'MEETING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('6', 'Initial outreach to Signal Forge', 'Sent introductory email about analytics capabilities.', 'EMAIL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('7', 'Follow-up call with Sophia Chen', 'Discussed Northstar Labs expansion plans and additional seats.', 'CALL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO activities (customer_id, title, description, type, created_at, updated_at, user_id) VALUES ('8', 'Campaign performance review', 'Reviewed digital campaign results with James Wilson.', 'NOTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- Emails
INSERT INTO emails (customer_id, subject, body, from_address, to_address, status, sent_at, created_at, user_id) VALUES ('1', 'Welcome to CRM Platform', 'Dear Alice, welcome to our platform. We are excited to have you on board.', 'sales@crm.com', 'alice@northstar.com', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO emails (customer_id, subject, body, from_address, to_address, status, sent_at, created_at, user_id) VALUES ('2', 'Enterprise Proposal - Verve Studio', 'Hi Marcus, please find attached the enterprise proposal for Verve Studio.', 'sales@crm.com', 'marcus@verve.com', 'OPENED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO emails (customer_id, subject, body, from_address, to_address, status, sent_at, created_at, user_id) VALUES ('3', 'Contract Renewal - Lumen HQ', 'Dear Priya, your contract is up for renewal.Lets schedule a call to discuss.', 'accounts@crm.com', 'priya@lumen.com', 'REPLIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO emails (customer_id, subject, body, from_address, to_address, status, sent_at, created_at, user_id) VALUES ('4', 'Integration Guide - NovaWorks', 'Hi Carlos, here is the integration guide for the NovaWorks API setup.', 'support@crm.com', 'carlos@novaworks.com', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO emails (customer_id, subject, body, from_address, to_address, status, sent_at, created_at, user_id) VALUES ('5', 'Monthly Report - Summit Labs', 'Dear Emma, attached is your monthly usage and performance report.', 'reports@crm.com', 'emma@summitlabs.com', 'RECEIVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
