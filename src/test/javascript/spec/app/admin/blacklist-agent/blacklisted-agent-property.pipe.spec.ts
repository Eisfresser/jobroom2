import { BlacklistedAgentPropertyFilterPipe } from '../../../../../../main/webapp/app/admin/blacklist-agent/blacklisted-agent-property-filter.pipe';
import {
    BlacklistedAgent,
    BlacklistedAgentStatus
} from '../../../../../../main/webapp/app/admin/blacklist-agent/blacklisted.agent.model';

describe('BlacklistedAgentPropertyFilterPipe', () => {
    let pipe: BlacklistedAgentPropertyFilterPipe;

    beforeEach(() => {
        pipe = new BlacklistedAgentPropertyFilterPipe();
    });

    it('should return empty list if no blacklisted agents', () => {
        // GIVEN
        const agents : BlacklistedAgent[] = null;

        // WHEN
        const result = pipe.transform(agents);

        // THEN
        expect(result.length).toEqual(0);
    });


    it('should the same list of blacklisted agents if no filterText', () => {
        const agent = createAgent();

        // GIVEN
        const agents : BlacklistedAgent[] = [agent];

        // WHEN
        const result = pipe.transform(agents);

        // THEN
        expect(result).toEqual(agents);
    });

    it('should filter list of blacklisted agents against agent with property which contains filter text.', () => {
        const agent = createAgent();
        const agent2 = createAgent();
        agent2.name = 'zz';
        // GIVEN
        const agents : BlacklistedAgent[] = [agent, agent2];

        // WHEN
        const result = pipe.transform(agents, 'zz');

        // THEN
        expect(result.length).toEqual(1);
        expect(result).toContain(agent2)
    });

    let createAgent = () => ({
        id: 'id',
        externalId: 'externalId',
        status: BlacklistedAgentStatus.INACTIVE,
        name: 'name',
        street: 'street',
        zipCode: '8130',
        city: 'city',
        createdBy: 'createdBy',
        blacklistedAt: new Date('2018-10-06T10:00:45.409'),
        blacklistingCounter: 2
    }) as BlacklistedAgent;
});
