import { Pipe, PipeTransform } from '@angular/core';
import { BlacklistedAgent } from './blacklisted.agent.model';

@Pipe({
    name: 'blacklistedAgentPropertyFilter'
})
export class BlacklistedAgentPropertyFilterPipe implements PipeTransform {

    transform(agents: BlacklistedAgent[], filterText?: string): BlacklistedAgent[] {
        if (agents) {
            if (filterText) {
                return agents.filter(
                    (agent) => this.properties(agent)
                        .some((property) => property.toString()
                            .includes(filterText)));
            }
            return agents;
        }
        return [];
    }

    private properties(agent: BlacklistedAgent) {
        return Object.keys(agent)
            .map((key) => agent[key]);
    }
}
